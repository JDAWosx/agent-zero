package com.agentzero.android.ui

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.agentzero.android.R
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val vm: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val messagesContainer = findViewById<LinearLayout>(R.id.messagesContainer)
        val scrollView = findViewById<ScrollView>(R.id.scrollView)
        val messageInput = findViewById<EditText>(R.id.messageInput)
        val sendButton = findViewById<ImageButton>(R.id.sendButton)

        lifecycleScope.launch {
            vm.messages.collectLatest { msgs ->
                messagesContainer.removeAllViews()
                msgs.forEach { msg ->
                    val tv = TextView(this@MainActivity)
                    tv.text = msg
                    messagesContainer.addView(tv)
                }
                scrollView.post { scrollView.fullScroll(ScrollView.FOCUS_DOWN) }
            }
        }

        sendButton.setOnClickListener {
            val text = messageInput.text?.toString()?.trim().orEmpty()
            if (text.isNotEmpty()) {
                vm.sendMessage(text)
                messageInput.text?.clear()
            }
        }

        // Load saved endpoints
        val prefs = getSharedPreferences("agentzero_prefs", Context.MODE_PRIVATE)
        val cloudBase = prefs.getString("cloud_base", null)
        val cloudKey = prefs.getString("cloud_key", null)
        val ollamaBase = prefs.getString("ollama_base", null)
        vm.updateConfig(cloudBase, cloudKey, ollamaBase)
    }
}
