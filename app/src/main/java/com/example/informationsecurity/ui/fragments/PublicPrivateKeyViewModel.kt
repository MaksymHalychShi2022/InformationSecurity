package com.example.informationsecurity.ui.fragments

import android.app.Application
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.util.Base64

open class PublicPrivateKeyViewModel(application: Application) : BaseViewModel(application) {
    val publicKey = MutableLiveData<String>()
    val privateKey = MutableLiveData<String>()

    fun generateKeys(algorithm: String) {
        // Generate Key Pair using DSA
        val keyPairGenerator = KeyPairGenerator.getInstance(algorithm) // "DSA" or "RSA"
        keyPairGenerator.initialize(2048) // 2048-bit key size for better security
        val keyPair: KeyPair = keyPairGenerator.generateKeyPair()

        // Convert keys to Base64 strings for readability
        val publicKeyEncoded = Base64.getEncoder().encodeToString(keyPair.public.encoded)
        val privateKeyEncoded = Base64.getEncoder().encodeToString(keyPair.private.encoded)

        // Update LiveData for keys
        publicKey.postValue(publicKeyEncoded)
        privateKey.postValue(privateKeyEncoded)
    }

    suspend fun savePublicKey(uri: Uri) = publicKey.value?.let {
        writeToFile(uri, it)
    } ?: throw Exception("Public key is not available")

    suspend fun savePrivateKey(uri: Uri) = privateKey.value?.let {
        writeToFile(uri, it)
    } ?: throw Exception("Private key is not available")

    suspend fun loadPublicKey(uri: Uri) = publicKey.postValue(readFromFile(uri))

    suspend fun loadPrivateKey(uri: Uri) = privateKey.postValue(readFromFile(uri))
}