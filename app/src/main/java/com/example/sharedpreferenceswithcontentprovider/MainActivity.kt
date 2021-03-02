package com.example.sharedpreferenceswithcontentprovider

import android.content.ContentValues
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.core.text.set

const val SHARED_KEY = "preferences"

class MainActivity : AppCompatActivity() {

    lateinit var saveButton: Button
    lateinit var editText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        editText = findViewById(R.id.edit_text)
        saveButton = findViewById(R.id.save_btn)

        val cursor = contentResolver?.query(SharedPreferencesProvider.CONTENT_URI, null, null, null, null)
        cursor?.let {
            if (it.moveToFirst()) {
                editText.setText(it.getString(0))
            }
        }
        cursor?.close()

        saveButton.setOnClickListener {
            val contentValues = ContentValues()
            contentValues.put(SHARED_KEY, editText.text.toString())
            contentResolver?.update(SharedPreferencesProvider.CONTENT_URI, contentValues, null, null)
        }

    }
}