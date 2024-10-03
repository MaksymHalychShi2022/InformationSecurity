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
import java.security.MessageDigest

class Lab2ViewModel(application: Application) : AndroidViewModel(application) {
    private val md = MessageDigest.getInstance("MD5")

    private val _output = MutableLiveData<String>().apply {
        value = "Output md hash will be here!"
    }
    val output: LiveData<String> = _output

    fun hash(input: String): LiveData<OperationState<Unit>> {
        val operationState = MutableLiveData<OperationState<Unit>>()
        viewModelScope.launch {
            operationState.postValue(OperationState.Loading())
            try {
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

    fun hash(uri: Uri): LiveData<OperationState<Unit>> {
        val operationState = MutableLiveData<OperationState<Unit>>()
        viewModelScope.launch {
            operationState.postValue(OperationState.Loading())
            try {
                val byteArray = readFileToByteArray(uri)

                // Handle the case where the file could not be read (byteArray is null)
                if (byteArray == null || byteArray.isEmpty()) {
                    throw Exception("Failed to read the file or file is empty")
                }

                val hash = md.digest(byteArray).joinToString("") {
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

    // Function to read file from Uri and return ByteArray
    private suspend fun readFileToByteArray(uri: Uri): ByteArray? {
        // Access ContentResolver from the application context
        val contentResolver: ContentResolver = getApplication<Application>().contentResolver

        return withContext(Dispatchers.IO) {
            try {
                val inputStream = contentResolver.openInputStream(uri)
                inputStream?.use { it.readBytes() }
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }
}