package com.example.informationsecurity.ui.lab3


import android.app.Application
import androidx.lifecycle.AndroidViewModel
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec
import android.net.Uri
import android.content.ContentResolver
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.informationsecurity.utils.OperationState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.io.OutputStream
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import javax.crypto.KeyGenerator
import javax.crypto.spec.IvParameterSpec
import java.security.SecureRandom

class Lab3ViewModel(application: Application) : AndroidViewModel(application) {
    private val _passphrase = MutableLiveData<String>().apply {
        value = "This is the pass phrase"
    }
    val passphrase: LiveData<String> = _passphrase


    fun encryptFile(
        inputUri: Uri,
        wordSize: Int = 64,
        rounds: Int = 12,
        keyLength: Int = 8
    ): LiveData<OperationState<Unit>> {
        val operationState = MutableLiveData<OperationState<Unit>>()
        viewModelScope.launch {
            operationState.postValue(OperationState.Loading())  // Show loading state

            withContext(Dispatchers.IO) {
                try {
                    delay(2000)

                    operationState.postValue(OperationState.Success(Unit))
                } catch (e: Exception) {
                    e.printStackTrace()
                    operationState.postValue(OperationState.Error("Error: ${e.message}"))  // Error occurred
                }
            }
        }
        return operationState
    }

    fun decryptFile(
        inputUri: Uri,
        wordSize: Int = 64,
        rounds: Int = 12,
        keyLength: Int = 8
    ): LiveData<OperationState<Unit>> {
        val operationState = MutableLiveData<OperationState<Unit>>()
        viewModelScope.launch {
            operationState.postValue(OperationState.Loading())  // Show loading state

            withContext(Dispatchers.IO) {
                try {
                    delay(2000)

                    operationState.postValue(OperationState.Success(Unit))
                } catch (e: Exception) {
                    e.printStackTrace()
                    operationState.postValue(OperationState.Error("Error: ${e.message}"))  // Error occurred
                }
            }
        }
        return operationState
    }

}
