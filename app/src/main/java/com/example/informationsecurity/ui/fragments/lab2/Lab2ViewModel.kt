package com.example.informationsecurity.ui.fragments.lab2

import android.app.Application
import android.content.ContentResolver
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.informationsecurity.ui.fragments.BaseViewModel
import com.example.informationsecurity.utils.MD5
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream

class Lab2ViewModel(application: Application) : BaseViewModel(application) {

    private val _hash = MutableLiveData<String>()
    val hash: LiveData<String> = _hash

    suspend fun saveHash(uri: Uri) = hash.value?.let {
        writeToFile(uri, it)
    } ?: throw Exception("Hash is not available")

    suspend fun loadHash(uri: Uri) = _hash.postValue(readFromFile(uri))

    fun hash(input: String) {
        val md = MD5()
        val hash = md.digest(input.toByteArray()).joinToString("") {
            "%02x".format(it)
        }
        _hash.value = hash
    }


    // Function to calculate the hash and return LiveData with the operation state
    suspend fun hash(uri: Uri) {
        // Initialize the MD5 digest algorithm
        val md5 = MD5()

        val contentResolver: ContentResolver = getApplication<Application>().contentResolver

        withContext(Dispatchers.IO) {
            val inputStream: InputStream? = contentResolver.openInputStream(uri)
            inputStream?.use { stream ->
                val buffer = ByteArray(8192)  // 8 KB buffer size
                var bytesRead: Int

                // Read the file in chunks and update the digest
                while (stream.read(buffer).also { bytesRead = it } != -1) {
                    md5.update(buffer, 0, bytesRead)  // Update digest with each chunk
                }
            } ?: throw Exception("Failed to open input stream")
        }

        // Get the final hash after processing the entire file
        val hash = md5.digest().joinToString("") {
            "%02x".format(it)
        }

        // Update the LiveData with the hash result
        _hash.value = hash
    }
}