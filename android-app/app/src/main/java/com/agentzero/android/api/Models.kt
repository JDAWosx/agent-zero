package com.agentzero.android.api

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ChatRequest(
    val model: String,
    val messages: List<Message>
)

@Serializable
data class Message(
    val role: String,
    val content: String
)

@Serializable
data class ChatResponse(
    val id: String? = null,
    val created: Long? = null,
    val model: String? = null,
    val choices: List<Choice>? = null,
    val response: String? = null // For some servers like Ollama's /generate
)

@Serializable
data class Choice(
    val index: Int,
    val message: Message? = null,
    val delta: Message? = null,
    @SerialName("finish_reason") val finishReason: String? = null
)
