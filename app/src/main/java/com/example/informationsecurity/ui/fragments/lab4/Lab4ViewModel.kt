package com.example.informationsecurity.ui.fragments.lab4

import android.app.Application
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.informationsecurity.ui.MainViewModel
import java.security.KeyFactory
import java.security.KeyPairGenerator
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import java.util.Base64
import javax.crypto.Cipher

class Lab4ViewModel(application: Application) : MainViewModel(application) {
    private val _publicKey = MutableLiveData<String>().apply {
        value = "This is the public key"
    }
    val publicKey: LiveData<String> = _publicKey

    private val _privateKey = MutableLiveData<String>().apply {
        value = "This is the private key"
    }
    val privateKey: LiveData<String> = _privateKey

    suspend fun loadPublicKey(uri: Uri) {
        val content = readFromFile(uri)
        _publicKey.postValue(content)
    }

    suspend fun loadPrivateKey(uri: Uri) {
        val content = readFromFile(uri)
        _privateKey.postValue(content)
    }


    suspend fun generateKeys() {
        val keyPairGenerator = KeyPairGenerator.getInstance("RSA")
        keyPairGenerator.initialize(2048)
        val keyPair = keyPairGenerator.generateKeyPair()

        val publicKey = Base64.getEncoder().encodeToString(keyPair.public.encoded)
        val privateKey = Base64.getEncoder().encodeToString(keyPair.private.encoded)

        _publicKey.postValue(publicKey)
        _privateKey.postValue(privateKey)
    }

    suspend fun savePublicKey(uri: Uri) {
        val publicKeyContent = _publicKey.value ?: throw Exception("Public key is not available")
        writeToFile(uri, publicKeyContent)
    }

    suspend fun savePrivateKey(uri: Uri) {
        val privateKeyContent = _privateKey.value ?: throw Exception("Private key is not available")
        writeToFile(uri, privateKeyContent)
    }


    fun encryptFile(inputUri: Uri, outputUri: Uri) {
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
                val buffer =
                    ByteArray(245) // RSA can handle up to 245 bytes for 2048-bit keys
                var bytesRead: Int
                while (input.read(buffer).also { bytesRead = it } != -1) {
                    val actualData = buffer.copyOfRange(0, bytesRead)
                    val encryptedChunk = cipher.doFinal(actualData)
                    output.write(encryptedChunk)
                }
            } ?: throw Exception("Unable to open output stream")
        } ?: throw Exception("Unable to open input stream")
    }

    fun decryptFile(inputUri: Uri, outputUri: Uri) {
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
            } ?: throw Exception("Unable to open output stream")
        } ?: throw Exception("Unable to open input stream")
    }
}
