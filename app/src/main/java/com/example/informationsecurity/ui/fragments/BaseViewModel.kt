package com.example.informationsecurity.ui.fragments

import android.app.Application
import android.content.ContentResolver
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.OutputStream

abstract class BaseViewModel(application: Application) : AndroidViewModel(application) {
    suspend fun writeToFile(uri: Uri, content: String) {
        withContext(Dispatchers.IO) {
            // Access ContentResolver from application context
            val contentResolver: ContentResolver = getApplication<Application>().contentResolver
            val outputStream: OutputStream? = contentResolver.openOutputStream(uri)
            outputStream?.use {
                it.write(content.toByteArray())
            } ?: throw Exception("Unable to open InputStream")
        }
    }

    suspend fun readFromFile(uri: Uri): String {
        return withContext(Dispatchers.IO) {
            val contentResolver = getApplication<Application>().contentResolver
            val inputStream = contentResolver.openInputStream(uri)
            inputStream?.use {
                it.bufferedReader().use { reader -> reader.readText() }
            } ?: throw Exception("Unable to open InputStream")
        }
    }
}