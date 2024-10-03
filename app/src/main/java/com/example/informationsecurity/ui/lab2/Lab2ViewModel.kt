package com.example.informationsecurity.ui.lab2

import android.app.Application
import android.content.ContentResolver
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.informationsecurity.utils.OperationState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.security.MessageDigest

class Lab2ViewModel(application: Application) : AndroidViewModel(application) {


    private val _output = MutableLiveData<String>().apply {
        value = "Output md hash will be here!"
    }
    val output: LiveData<String> = _output

    fun hash(input: String): LiveData<OperationState<Unit>> {
        val operationState = MutableLiveData<OperationState<Unit>>()
        viewModelScope.launch {
            operationState.postValue(OperationState.Loading())
            try {
                val md = MessageDigest.getInstance("MD5")
                val hash = md.digest(input.toByteArray()).joinToString("") {
                    "%02x".format(it)
                }
                _output.apply {
                    value = hash
                }
                operationState.postValue(OperationState.Success(Unit))
            } catch (e: Exception) {
                operationState.postValue(OperationState.Error("Unknown error ${e.message}"))
            }
        }
        return operationState
    }

    // Function to calculate the hash and return LiveData with the operation state
    fun hash(uri: Uri): LiveData<OperationState<Unit>> {
        val operationState = MutableLiveData<OperationState<Unit>>()

        viewModelScope.launch {
            operationState.postValue(OperationState.Loading())  // Indicate loading state

            try {
                // Initialize the MD5 digest algorithm
                val md = MessageDigest.getInstance("MD5")

                // Process the file in chunks and update the digest
                processFileInChunks(uri, md)

                // Get the final hash after processing the entire file
                val hash = md.digest().joinToString("") {
                    "%02x".format(it)
                }

                // Update the LiveData with the hash result
                _output.value = hash

                // Notify that the operation succeeded
                operationState.postValue(OperationState.Success(Unit))
            } catch (e: Exception) {
                // Handle errors and notify via LiveData
                operationState.postValue(OperationState.Error("Error processing file: ${e.message}"))
            }
        }

        return operationState
    }

    // Function to process a file in chunks and update the MessageDigest
    private suspend fun processFileInChunks(uri: Uri, digest: MessageDigest) {
        val contentResolver: ContentResolver = getApplication<Application>().contentResolver

        withContext(Dispatchers.IO) {
            val inputStream: InputStream? = contentResolver.openInputStream(uri)
            inputStream?.use { stream ->
                val buffer = ByteArray(8192)  // 8 KB buffer size
                var bytesRead: Int

                // Read the file in chunks and update the digest
                while (stream.read(buffer).also { bytesRead = it } != -1) {
                    digest.update(buffer, 0, bytesRead)  // Update digest with each chunk
                }
            } ?: throw Exception("Failed to open input stream")
        }
    }
}