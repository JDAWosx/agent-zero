package com.agentzero.android.api

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.logging.HttpLoggingInterceptor
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class AIRepository {
    private var cloudBase: String? = null
    private var cloudKey: String? = null
    private var ollamaBase: String? = null

    private val jsonMedia = "application/json; charset=utf-8".toMediaType()

    private val client: OkHttpClient by lazy {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }
        OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .build()
    }

    fun updateConfig(cloudBase: String?, cloudKey: String?, ollamaBase: String?) {
        this.cloudBase = cloudBase?.trim()?.ifEmpty { null }
        this.cloudKey = cloudKey?.trim()?.ifEmpty { null }
        this.ollamaBase = ollamaBase?.trim()?.ifEmpty { null }
    }

    suspend fun chat(prompt: String, model: String = "gpt-4o-mini"): String = withContext(Dispatchers.IO) {
        // Prefer local Ollama if configured, else Cloud API if configured
        val ollama = ollamaBase
        if (!ollama.isNullOrBlank()) {
            return@withContext callOllama(ollama, prompt, model)
        }
        val cloud = cloudBase
        val key = cloudKey
        if (!cloud.isNullOrBlank() && !key.isNullOrBlank()) {
            return@withContext callCloud(cloud, key, prompt, model)
        }
        return@withContext "No endpoints configured. Open settings to add Cloud API/Ollama."
    }

    private fun callCloud(base: String, key: String, prompt: String, model: String): String {
        val url = if (base.endsWith("/")) base + "v1/chat/completions" else "$base/v1/chat/completions"
        val payload = JSONObject(
            mapOf(
                "model" to model,
                "messages" to listOf(mapOf("role" to "user", "content" to prompt))
            )
        ).toString()

        val req = Request.Builder()
            .url(url)
            .addHeader("Authorization", "Bearer $key")
            .addHeader("Content-Type", "application/json")
            .post(payload.toRequestBody(jsonMedia))
            .build()
        client.newCall(req).execute().use { resp ->
            if (!resp.isSuccessful) return "Cloud API error: ${resp.code} ${resp.message}"
            val body = resp.body?.string().orEmpty()
            // Try to parse OpenAI-like response
            return parseOpenAIResponse(body)
        }
    }

    private fun callOllama(base: String, prompt: String, model: String): String {
        // Ollama has two key endpoints: /api/chat and /api/generate depending on version
        // We'll try chat first, then fallback to generate
        val baseUrl = if (base.endsWith("/")) base.trimEnd('/') else base
        val chatUrl = "$baseUrl/api/chat"
        val genUrl = "$baseUrl/api/generate"
        val chatPayload = JSONObject(
            mapOf(
                "model" to model,
                "messages" to listOf(mapOf("role" to "user", "content" to prompt))
            )
        ).toString()
        val genPayload = JSONObject(
            mapOf(
                "model" to model,
                "prompt" to prompt,
                "stream" to false
            )
        ).toString()

        val chatReq = Request.Builder()
            .url(chatUrl)
            .addHeader("Content-Type", "application/json")
            .post(chatPayload.toRequestBody(jsonMedia))
            .build()
        client.newCall(chatReq).execute().use { resp ->
            if (resp.isSuccessful) {
                val body = resp.body?.string().orEmpty()
                return parseOllamaChat(body)
            }
        }

        val genReq = Request.Builder()
            .url(genUrl)
            .addHeader("Content-Type", "application/json")
            .post(genPayload.toRequestBody(jsonMedia))
            .build()
        client.newCall(genReq).execute().use { resp ->
            if (!resp.isSuccessful) return "Ollama error: ${resp.code} ${resp.message}"
            val body = resp.body?.string().orEmpty()
            return parseOllamaGenerate(body)
        }
    }

    private fun parseOpenAIResponse(body: String): String {
        // Minimal parsing without extra deps
        // Attempt to extract message.content from first choice
        try {
            val root = org.json.JSONObject(body)
            val choices = root.optJSONArray("choices")
            if (choices != null && choices.length() > 0) {
                val first = choices.getJSONObject(0)
                val msg = first.optJSONObject("message")
                val content = msg?.optString("content")
                if (!content.isNullOrEmpty()) return content
            }
        } catch (_: Throwable) {}
        return body.take(2000)
    }

    private fun parseOllamaChat(body: String): String {
        try {
            val root = org.json.JSONObject(body)
            val message = root.optJSONObject("message")
            val content = message?.optString("content")
            if (!content.isNullOrEmpty()) return content
        } catch (_: Throwable) {}
        return body.take(2000)
    }

    private fun parseOllamaGenerate(body: String): String {
        try {
            val root = org.json.JSONObject(body)
            val response = root.optString("response")
            if (!response.isNullOrEmpty()) return response
        } catch (_: Throwable) {}
        return body.take(2000)
    }
}
