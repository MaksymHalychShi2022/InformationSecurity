package com.example.informationsecurity.ui.fragments.lab5

import android.app.Application
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.informationsecurity.ui.fragments.PublicPrivateKeyViewModel
import java.security.KeyFactory
import java.security.Signature
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import java.util.Base64

class Lab5ViewModel(application: Application) : PublicPrivateKeyViewModel(application) {
    private val _signature = MutableLiveData<String>()
    val signature: LiveData<String> = _signature

    suspend fun saveSignature(uri: Uri) = _signature.value?.let {
        writeToFile(uri, it)
    } ?: throw Exception("Signature is not available")

    suspend fun loadSignature(uri: Uri) = _signature.postValue(readFromFile(uri))

    fun sign(inputString: String) {
        // Ensure keys are available
        val privateKeyEncoded = privateKey.value ?: throw Exception("Private key is not available")

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

    suspend fun sign(uri: Uri) = sign(readFromFile(uri))

    fun verifySignature(inputData: String): Boolean {
        // Ensure the public key and signature are available
        val publicKeyEncoded = publicKey.value ?: throw Exception("Public key is not available")
        val storedSignature = _signature.value ?: throw Exception("Signature is not available")

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

        return signature.verify(digitalSignature)
    }

    suspend fun verifySignature(uri: Uri): Boolean = verifySignature(readFromFile(uri))
}
