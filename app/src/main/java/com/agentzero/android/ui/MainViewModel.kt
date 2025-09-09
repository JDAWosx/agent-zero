package com.agentzero.android.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.agentzero.android.api.AIRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    private val repo = AIRepository()
    private val _messages = MutableStateFlow<List<String>>(emptyList())
    private var currentModel: String? = null
    val messages: StateFlow<List<String>> = _messages

    fun sendMessage(userText: String) {
        append("You: $userText")
        viewModelScope.launch {
            try {
                repo.chatStream(userText, currentModel ?: "gpt-4o-mini").collect { chunk ->
                    if (chunk.isNotBlank()) append("AI: $chunk")
                }
            } catch (t: Throwable) {
                append("Error: ${t.message}")
            }
        }
    }

    fun setModel(model: String?) { currentModel = model }

    fun updateConfig(cloudBase: String?, cloudKey: String?, ollamaBase: String?) {
        repo.updateConfig(cloudBase, cloudKey, ollamaBase)
    }

    private fun append(line: String) {
        _messages.value = _messages.value + line
    }
}
