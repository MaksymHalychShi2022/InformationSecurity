package com.example.informationsecurity.ui

import android.app.Application
import android.content.ContentResolver
import android.net.Uri
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.OutputStream

open class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val _isVisibleProgressBar = MutableLiveData<Boolean>().apply {
        value = false
    }
    val isVisibleProgressBar: LiveData<Boolean> = _isVisibleProgressBar

    fun <T> runWithProgress(
        task: suspend () -> T,
        onSuccess: ((T) -> Unit)? = null,
        onSuccessMessage: String? = null,
        onError: ((Throwable) -> Unit)? = {
            Toast.makeText(
                getApplication(),
                "Error: ${it.message}",
                Toast.LENGTH_LONG
            ).show()
        }
    ) {
        viewModelScope.launch {
            _isVisibleProgressBar.value = true // Show progress bar
            try {
                val result = task() // Execute the suspend function
                onSuccessMessage?.let {
                    Toast.makeText(getApplication(), it, Toast.LENGTH_SHORT).show()
                }
                onSuccess?.invoke(result) // Call onSuccess if provided
            } catch (e: Throwable) {
                onError?.invoke(e) // Call onError if provided
            } finally {
                _isVisibleProgressBar.value = false // Hide progress bar
            }
        }
    }

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