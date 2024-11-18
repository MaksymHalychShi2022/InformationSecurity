package com.example.informationsecurity.ui.fragments.lab5

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.informationsecurity.utils.OperationState
import kotlinx.coroutines.launch
import java.security.KeyFactory
import java.security.KeyPairGenerator
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import java.util.Base64
import javax.crypto.Cipher

class Lab5ViewModel(application: Application) : AndroidViewModel(application) {
    private val _signatureOutput = MutableLiveData<String>().apply {
        value = "This is the digital signature"
    }
    val signatureOutput: LiveData<String> = _signatureOutput


    private val _publicKey = MutableLiveData<String>().apply {
        value = "This is the public key"
    }
    val publicKey: LiveData<String> = _publicKey

    private val _privateKey = MutableLiveData<String>().apply {
        value = "This is the private key"
    }
    val privateKey: LiveData<String> = _privateKey

    fun generateKeys(): LiveData<OperationState<Unit>> {
        val operationState = MutableLiveData<OperationState<Unit>>()

        viewModelScope.launch {
            operationState.postValue(OperationState.Loading())
            try {
                ///

                operationState.postValue(OperationState.Success(Unit))
            } catch (e: Exception) {
                operationState.postValue(OperationState.Error("Error generating keys: ${e.message}"))
            }
        }

        return operationState
    }

    fun loadPublicKey(uri: Uri): LiveData<OperationState<Unit>> {
        val operationState = MutableLiveData<OperationState<Unit>>()

        viewModelScope.launch {
            operationState.postValue(OperationState.Loading())
            try {
                // Open an InputStream to read the content from the Uri
                val contentResolver = getApplication<Application>().contentResolver
                val inputStream = contentResolver.openInputStream(uri)

                inputStream?.use {
                    val content = it.bufferedReader().use { reader -> reader.readText() }
                    _publicKey.postValue(content) // Save the read content to publicKey LiveData
                    operationState.postValue(OperationState.Success(Unit))
                } ?: throw Exception("Unable to open InputStream")

            } catch (e: Exception) {
                operationState.postValue(OperationState.Error("Unknown error: ${e.message}"))
            }
        }

        return operationState
    }

    fun loadPrivateKey(uri: Uri): LiveData<OperationState<Unit>> {
        val operationState = MutableLiveData<OperationState<Unit>>()

        viewModelScope.launch {
            operationState.postValue(OperationState.Loading())
            try {
                // Open an InputStream to read the content from the Uri
                val contentResolver = getApplication<Application>().contentResolver
                val inputStream = contentResolver.openInputStream(uri)

                inputStream?.use {
                    val content = it.bufferedReader().use { reader -> reader.readText() }
                    _privateKey.postValue(content) // Save the read content to privateKey LiveData
                    operationState.postValue(OperationState.Success(Unit))
                } ?: throw Exception("Unable to open InputStream")

            } catch (e: Exception) {
                operationState.postValue(OperationState.Error("Unknown error: ${e.message}"))
            }
        }

        return operationState
    }

    fun savePublicKey(uri: Uri): LiveData<OperationState<Unit>> {
        val operationState = MutableLiveData<OperationState<Unit>>()

        viewModelScope.launch {
            operationState.postValue(OperationState.Loading())
            try {
                val publicKeyContent =
                    _publicKey.value ?: throw Exception("Public key is not available")
                val contentResolver = getApplication<Application>().contentResolver
                val outputStream = contentResolver.openOutputStream(uri)

                outputStream?.use {
                    it.bufferedWriter().use { writer -> writer.write(publicKeyContent) }
                    operationState.postValue(OperationState.Success(Unit))
                } ?: throw Exception("Unable to open OutputStream")

            } catch (e: Exception) {
                operationState.postValue(OperationState.Error("Error saving public key: ${e.message}"))
            }
        }

        return operationState
    }

    fun savePrivateKey(uri: Uri): LiveData<OperationState<Unit>> {
        val operationState = MutableLiveData<OperationState<Unit>>()

        viewModelScope.launch {
            operationState.postValue(OperationState.Loading())
            try {
                val privateKeyContent =
                    _privateKey.value ?: throw Exception("Private key is not available")
                val contentResolver = getApplication<Application>().contentResolver
                val outputStream = contentResolver.openOutputStream(uri)

                outputStream?.use {
                    it.bufferedWriter().use { writer -> writer.write(privateKeyContent) }
                    operationState.postValue(OperationState.Success(Unit))
                } ?: throw Exception("Unable to open OutputStream")

            } catch (e: Exception) {
                operationState.postValue(OperationState.Error("Error saving private key: ${e.message}"))
            }
        }

        return operationState
    }

    fun sign(inputString: String): LiveData<OperationState<Unit>> {
        val operationState = MutableLiveData<OperationState<Unit>>()

        viewModelScope.launch {
            operationState.postValue(OperationState.Loading())
            try {
                ///
                operationState.postValue(OperationState.Success(Unit))
            } catch (e: Exception) {
                operationState.postValue(OperationState.Error("Error: ${e.message}"))
            }
        }

        return operationState
    }

    fun sign(uri: Uri): LiveData<OperationState<Unit>> {
        val operationState = MutableLiveData<OperationState<Unit>>()

        viewModelScope.launch {
            operationState.postValue(OperationState.Loading())
            try {
                ///
                operationState.postValue(OperationState.Success(Unit))
            } catch (e: Exception) {
                operationState.postValue(OperationState.Error("Error: ${e.message}"))
            }
        }

        return operationState
    }

    fun checkSignature(uri: Uri): LiveData<OperationState<Unit>> {
        val operationState = MutableLiveData<OperationState<Unit>>()

        viewModelScope.launch {
            operationState.postValue(OperationState.Loading())
            try {
                ///
                operationState.postValue(OperationState.Success(Unit))
            } catch (e: Exception) {
                operationState.postValue(OperationState.Error("Error: ${e.message}"))
            }
        }

        return operationState
    }

}
