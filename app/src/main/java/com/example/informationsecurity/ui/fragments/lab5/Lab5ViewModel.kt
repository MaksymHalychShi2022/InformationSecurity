package com.example.informationsecurity.ui.fragments.lab5

import android.app.Application
import android.content.ContentResolver
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.informationsecurity.ui.MainViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.security.KeyFactory
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.Signature
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import java.util.Base64

class Lab5ViewModel(application: Application) : MainViewModel(application) {
    private val _signature = MutableLiveData<String>().apply {
        value = "This is the digital signature"
    }
    val signature: LiveData<String> = _signature


    private val _publicKey = MutableLiveData<String>().apply {
        value = "This is the public key"
    }
    val publicKey: LiveData<String> = _publicKey

    private val _privateKey = MutableLiveData<String>().apply {
        value = "This is the private key"
    }
    val privateKey: LiveData<String> = _privateKey

    suspend fun generateKeys() {
        // Generate Key Pair using DSA
        val keyPairGenerator = KeyPairGenerator.getInstance("DSA")
        keyPairGenerator.initialize(2048) // 2048-bit key size for better security
        val keyPair: KeyPair = keyPairGenerator.generateKeyPair()

        // Convert keys to Base64 strings for readability
        val publicKeyEncoded = Base64.getEncoder().encodeToString(keyPair.public.encoded)
        val privateKeyEncoded = Base64.getEncoder().encodeToString(keyPair.private.encoded)

        // Update LiveData for keys
        _publicKey.postValue(publicKeyEncoded)
        _privateKey.postValue(privateKeyEncoded)

    }

    suspend fun saveSignature(uri: Uri) {
        val signatureContent = _signature.value ?: throw Exception("Signature is not available")
        writeToFile(uri, signatureContent)
    }

    suspend fun savePublicKey(uri: Uri) {
        val publicKeyContent = _publicKey.value ?: throw Exception("Public key is not available")
        writeToFile(uri, publicKeyContent)
    }

    suspend fun savePrivateKey(uri: Uri) {
        val privateKeyContent = _privateKey.value ?: throw Exception("Private key is not available")
        writeToFile(uri, privateKeyContent)
    }

    suspend fun loadSignature(uri: Uri) {
        val content = readFromFile(uri)
        _signature.postValue(content)
    }

    suspend fun loadPublicKey(uri: Uri) {
        val content = readFromFile(uri)
        _publicKey.postValue(content)
    }

    suspend fun loadPrivateKey(uri: Uri) {
        val content = readFromFile(uri)
        _privateKey.postValue(content)
    }


    suspend fun sign(inputString: String) {
        // Ensure keys are available
        val privateKeyEncoded = _privateKey.value
        if (privateKeyEncoded.isNullOrEmpty()) {
            throw IllegalArgumentException("Private key not available")
        }

        // Decode the private key from Base64
        val privateKeyBytes = Base64.getDecoder().decode(privateKeyEncoded)
        val privateKey = KeyFactory.getInstance("DSA")
            .generatePrivate(PKCS8EncodedKeySpec(privateKeyBytes))

        // Initialize Signature with the private key
        val signature = Signature.getInstance("SHA256withDSA")
        signature.initSign(privateKey)

        // Update Signature with input string bytes
        signature.update(inputString.toByteArray(Charsets.UTF_8))

        // Generate the digital signature
        val digitalSignature = signature.sign()
        val digitalSignatureEncoded = Base64.getEncoder().encodeToString(digitalSignature)

        // Post the signature output
        _signature.postValue(digitalSignatureEncoded)
    }


    suspend fun sign(uri: Uri) {
        // Ensure keys are available
        val privateKeyEncoded = _privateKey.value
        if (privateKeyEncoded.isNullOrEmpty()) {
            throw Exception("Private key not available")
        }

        // Decode the private key from Base64
        val privateKeyBytes = Base64.getDecoder().decode(privateKeyEncoded)
        val privateKey = KeyFactory.getInstance("DSA")
            .generatePrivate(PKCS8EncodedKeySpec(privateKeyBytes))

        // Read file content from the URI
        val contentResolver: ContentResolver = getApplication<Application>().contentResolver
        val fileContent = withContext(Dispatchers.IO) {
            contentResolver.openInputStream(uri)?.use { it.readBytes() }
                ?: throw Exception("Unable to read file content")
        }

        // Initialize Signature with the private key
        val signature = Signature.getInstance("SHA256withDSA")
        signature.initSign(privateKey)
        signature.update(fileContent)

        // Generate the digital signature
        val digitalSignature = signature.sign()
        val digitalSignatureEncoded = Base64.getEncoder().encodeToString(digitalSignature)

        // Update the signature output
        _signature.postValue(digitalSignatureEncoded)

    }


    suspend fun verifySignature(inputData: String): Boolean {
        // Ensure the public key and signature are available
        val publicKeyEncoded = _publicKey.value
        val storedSignature = _signature.value

        if (publicKeyEncoded.isNullOrEmpty() || storedSignature.isNullOrEmpty()) {
            throw Exception("Public key or signature not available")
        }

        // Decode the public key from Base64
        val publicKeyBytes = Base64.getDecoder().decode(publicKeyEncoded)
        val publicKey = KeyFactory.getInstance("DSA")
            .generatePublic(X509EncodedKeySpec(publicKeyBytes))

        // Decode the signature from Base64
        val digitalSignature = Base64.getDecoder().decode(storedSignature)

        // Initialize Signature with the public key for verification
        val signature = Signature.getInstance("SHA256withDSA")
        signature.initVerify(publicKey)
        signature.update(inputData.toByteArray(Charsets.UTF_8))

        // Verify the signature
        return signature.verify(digitalSignature)

    }


    suspend fun verifyFileSignature(uri: Uri): Boolean {
        // Ensure the public key and signature are available
        val publicKeyEncoded = _publicKey.value
        val storedSignature = _signature.value

        if (publicKeyEncoded.isNullOrEmpty() || storedSignature.isNullOrEmpty()) {
            throw Exception("Public key or signature not available")
        }

        // Decode the public key from Base64
        val publicKeyBytes = Base64.getDecoder().decode(publicKeyEncoded)
        val publicKey = KeyFactory.getInstance("DSA")
            .generatePublic(X509EncodedKeySpec(publicKeyBytes))

        // Decode the signature from Base64
        val digitalSignature = Base64.getDecoder().decode(storedSignature)

        // Read the file content from the URI
        val contentResolver = getApplication<Application>().contentResolver
        val fileContent = contentResolver.openInputStream(uri)?.use { it.readBytes() }
            ?: throw Exception("Unable to read file content")

        // Initialize Signature with the public key for verification
        val signature = Signature.getInstance("SHA256withDSA")
        signature.initVerify(publicKey)
        signature.update(fileContent)

        // Verify the signature
        return signature.verify(digitalSignature)
    }
}
