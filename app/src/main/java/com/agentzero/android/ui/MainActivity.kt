package com.agentzero.android.ui

import android.content.Context
import android.os.Bundle
import android.content.Intent

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
        val btnSettings = findViewById<android.widget.Button>(R.id.btnSettings)

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
        val securePrefs = androidx.security.crypto.EncryptedSharedPreferences.create(
            this,
            "agentzero_secure_prefs",
            androidx.security.crypto.MasterKey.Builder(this).setKeyScheme(androidx.security.crypto.MasterKey.KeyScheme.AES256_GCM).build(),
            androidx.security.crypto.EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            androidx.security.crypto.EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
        val cloudBase = securePrefs.getString("cloud_base", null)
        val cloudKey = securePrefs.getString("cloud_key", null)
        val ollamaBase = securePrefs.getString("ollama_base", null)
        vm.updateConfig(cloudBase, cloudKey, ollamaBase)
        vm.setModel(securePrefs.getString("model_name", null))

        btnSettings.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
        btnSettings.setOnLongClickListener {
            // Model picker on long press
            val models = listOf(
                "gpt-4o-mini", "gpt-4o", "gpt-3.5-turbo",
                "llama3.1", "mistral", "phi3:latest"
            )
            ModelPickerDialog(this, models, null) { picked ->
                val securePrefs = androidx.security.crypto.EncryptedSharedPreferences.create(
                    this,
                    "agentzero_secure_prefs",
                    androidx.security.crypto.MasterKey.Builder(this).setKeyScheme(androidx.security.crypto.MasterKey.KeyScheme.AES256_GCM).build(),
                    androidx.security.crypto.EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    androidx.security.crypto.EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
                )
                securePrefs.edit().putString("model_name", picked).apply()
                vm.setModel(picked)
            }.show()
            true
        }
    }
}
