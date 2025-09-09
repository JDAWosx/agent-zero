package com.agentzero.android.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.agentzero.android.api.AIRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    private val repo = AIRepository()
    private val _messages = MutableStateFlow<List<String>>(emptyList())
    val messages: StateFlow<List<String>> = _messages

    fun sendMessage(userText: String) {
        append("You: $userText")
        viewModelScope.launch {
            try {
                val resp = repo.chat(userText)
                append("AI: ${resp}")
            } catch (t: Throwable) {
                append("Error: ${t.message}")
            }
        }
    }

    fun updateConfig(cloudBase: String?, cloudKey: String?, ollamaBase: String?) {
        repo.updateConfig(cloudBase, cloudKey, ollamaBase)
    }

    private fun append(line: String) {
        _messages.value = _messages.value + line
    }
}
