package com.agentzero.android.ui

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import com.agentzero.android.R

class ModelPickerDialog(
    private val context: Context,
    private val models: List<String>,
    private val current: String?,
    private val onPick: (String) -> Unit
) {
    fun show() {
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_model_picker, null)
        view.findViewById<TextView>(R.id.txtCurrentModel).text = context.getString(R.string.current_model) + ": " + (current ?: "default")
        val list = view.findViewById<ListView>(R.id.listModels)
        val adapter = ArrayAdapter(context, android.R.layout.simple_list_item_1, models)
        list.adapter = adapter

        val dialog = AlertDialog.Builder(context)
            .setTitle(R.string.choose_model)
            .setView(view)
            .setNegativeButton(android.R.string.cancel, null)
            .create()

        list.setOnItemClickListener { _, _, position, _ ->
            val m = models[position]
            onPick(m)
            dialog.dismiss()
        }
        dialog.show()
    }
}
