package com.agentzero.android.ui

import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.agentzero.android.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request

class SettingsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val editCloudBase = findViewById<EditText>(R.id.editCloudBase)
        val editCloudKey = findViewById<EditText>(R.id.editCloudKey)
        val editOllamaBase = findViewById<EditText>(R.id.editOllamaBase)
        val btnSave = findViewById<Button>(R.id.btnSave)
        val btnTest = findViewById<Button>(R.id.btnTest)
        val txtStatus = findViewById<TextView>(R.id.txtStatus)

        val masterKey = MasterKey.Builder(this)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
        val prefs = EncryptedSharedPreferences.create(
            this,
            "agentzero_secure_prefs",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
        editCloudBase.setText(prefs.getString("cloud_base", ""))
        editCloudKey.setText(prefs.getString("cloud_key", ""))
        editOllamaBase.setText(prefs.getString("ollama_base", ""))

        btnSave.setOnClickListener {
            prefs.edit()
                .putString("cloud_base", editCloudBase.text.toString().trim())
                .putString("cloud_key", editCloudKey.text.toString().trim())
                .putString("ollama_base", editOllamaBase.text.toString().trim())
                .apply()
            txtStatus.text = "Saved"
        }

        btnTest.setOnClickListener {
            txtStatus.text = "Testing..."
            val cloudBase = editCloudBase.text.toString().trim()
            val key = editCloudKey.text.toString().trim()
            val ollamaBase = editOllamaBase.text.toString().trim()
            CoroutineScope(Dispatchers.IO).launch {
                val client = OkHttpClient()
                var results = mutableListOf<String>()
                try {
                    if (cloudBase.isNotEmpty() && key.isNotEmpty()) {
                        val req = Request.Builder()
                            .url(if (cloudBase.endsWith("/")) cloudBase + "v1/models" else "$cloudBase/v1/models")
                            .addHeader("Authorization", "Bearer $key")
                            .build()
                        client.newCall(req).execute().use { resp ->
                            results.add("Cloud: ${resp.code} ${resp.message}")
                        }
                    }
                    if (ollamaBase.isNotEmpty()) {
                        val req = Request.Builder()
                            .url(ollamaBase.trimEnd('/') + "/api/tags")
                            .build()
                        client.newCall(req).execute().use { resp ->
                            results.add("Ollama: ${resp.code} ${resp.message}")
                        }
                    }
                } catch (t: Throwable) {
                    results.add("Error: ${t.message}")
                }
                launch(Dispatchers.Main) {
                    txtStatus.text = results.joinToString("\n")
                }
            }
        }
    }
}
