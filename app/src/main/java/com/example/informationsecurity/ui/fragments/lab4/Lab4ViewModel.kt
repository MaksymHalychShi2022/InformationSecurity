package com.example.informationsecurity.ui.fragments.lab4

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

class Lab4ViewModel(application: Application) : AndroidViewModel(application) {
    private val _publicKey = MutableLiveData<String>().apply {
        value = "This is the public key"
    }
    val publicKey: LiveData<String> = _publicKey

    private val _privateKey = MutableLiveData<String>().apply {
        value = "This is the private key"
    }
    val privateKey: LiveData<String> = _privateKey

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

    fun generateKeys(): LiveData<OperationState<Unit>> {
        val operationState = MutableLiveData<OperationState<Unit>>()

        viewModelScope.launch {
            operationState.postValue(OperationState.Loading())
            try {
                val keyPairGenerator = KeyPairGenerator.getInstance("RSA")
                keyPairGenerator.initialize(2048)
                val keyPair = keyPairGenerator.generateKeyPair()

                val publicKey = Base64.getEncoder().encodeToString(keyPair.public.encoded)
                val privateKey = Base64.getEncoder().encodeToString(keyPair.private.encoded)

                _publicKey.postValue(publicKey)
                _privateKey.postValue(privateKey)

                operationState.postValue(OperationState.Success(Unit))
            } catch (e: Exception) {
                operationState.postValue(OperationState.Error("Error generating keys: ${e.message}"))
            }
        }

        return operationState
    }

    fun savePublicKey(uri: Uri): LiveData<OperationState<Unit>> {
        val operationState = MutableLiveData<OperationState<Unit>>()

        viewModelScope.launch {
            operationState.postValue(OperationState.Loading())
            try {
                val publicKeyContent = _publicKey.value ?: throw Exception("Public key is not available")
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
                val privateKeyContent = _privateKey.value ?: throw Exception("Private key is not available")
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


    fun encryptFile(inputUri: Uri, outputUri: Uri): LiveData<OperationState<Unit>> {
        val operationState = MutableLiveData<OperationState<Unit>>()

        viewModelScope.launch {
            operationState.postValue(OperationState.Loading())
            try {
                val publicKeyString = _publicKey.value ?: throw Exception("Public key is not available")
                val publicKeyBytes = Base64.getDecoder().decode(publicKeyString)
                val keySpec = X509EncodedKeySpec(publicKeyBytes)
                val keyFactory = KeyFactory.getInstance("RSA")
                val publicKey = keyFactory.generatePublic(keySpec)

                val cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding")
                cipher.init(Cipher.ENCRYPT_MODE, publicKey)

                val contentResolver = getApplication<Application>().contentResolver
                val inputStream = contentResolver.openInputStream(inputUri)
                val outputStream = contentResolver.openOutputStream(outputUri)

                inputStream?.use { input ->
                    outputStream?.use { output ->
                        val buffer = ByteArray(245) // RSA can handle up to 245 bytes for 2048-bit keys
                        var bytesRead: Int
                        while (input.read(buffer).also { bytesRead = it } != -1) {
                            val actualData = buffer.copyOfRange(0, bytesRead)
                            val encryptedChunk = cipher.doFinal(actualData)
                            output.write(encryptedChunk)
                        }
                        operationState.postValue(OperationState.Success(Unit))
                    } ?: throw Exception("Unable to open output stream")
                } ?: throw Exception("Unable to open input stream")

            } catch (e: Exception) {
                operationState.postValue(OperationState.Error("Error encrypting file: ${e.message}"))
            }
        }

        return operationState
    }

    fun decryptFile(inputUri: Uri, outputUri: Uri): LiveData<OperationState<Unit>> {
        val operationState = MutableLiveData<OperationState<Unit>>()

        viewModelScope.launch {
            operationState.postValue(OperationState.Loading())
            try {
                val privateKeyString = _privateKey.value ?: throw Exception("Private key is not available")
                val privateKeyBytes = Base64.getDecoder().decode(privateKeyString)
                val keySpec = PKCS8EncodedKeySpec(privateKeyBytes)
                val keyFactory = KeyFactory.getInstance("RSA")
                val privateKey = keyFactory.generatePrivate(keySpec)

                val cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding")
                cipher.init(Cipher.DECRYPT_MODE, privateKey)

                val contentResolver = getApplication<Application>().contentResolver
                val inputStream = contentResolver.openInputStream(inputUri)
                val outputStream = contentResolver.openOutputStream(outputUri)

                inputStream?.use { input ->
                    outputStream?.use { output ->
                        val buffer = ByteArray(256) // Encrypted block size for 2048-bit keys
                        var bytesRead: Int
                        while (input.read(buffer).also { bytesRead = it } != -1) {
                            val actualData = buffer.copyOfRange(0, bytesRead)
                            val decryptedChunk = cipher.doFinal(actualData)
                            output.write(decryptedChunk)
                        }
                        operationState.postValue(OperationState.Success(Unit))
                    } ?: throw Exception("Unable to open output stream")
                } ?: throw Exception("Unable to open input stream")

            } catch (e: Exception) {
                operationState.postValue(OperationState.Error("Error decrypting file: ${e.message}"))
            }
        }

        return operationState
    }

}
