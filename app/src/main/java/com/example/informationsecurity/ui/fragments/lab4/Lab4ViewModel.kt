package com.example.informationsecurity.ui.fragments.lab4

import android.app.Application
import android.net.Uri
import com.example.informationsecurity.ui.fragments.PublicPrivateKeyViewModel
import java.security.Key
import java.security.KeyFactory
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import java.util.Base64
import javax.crypto.Cipher


class Lab4ViewModel(application: Application) : PublicPrivateKeyViewModel(application) {

    private fun getCipherInstance(mode: Int, key: Key): Cipher {
        val cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding")
        cipher.init(mode, key)
        return cipher
    }

    private fun processFile(
        inputUri: Uri,
        outputUri: Uri,
        cipher: Cipher,
        bufferSize: Int
    ) {
        val contentResolver = getApplication<Application>().contentResolver
        val inputStream = contentResolver.openInputStream(inputUri)
        val outputStream = contentResolver.openOutputStream(outputUri)

        inputStream?.use { input ->
            outputStream?.use { output ->
                val buffer = ByteArray(bufferSize)
                var bytesRead: Int
                while (input.read(buffer).also { bytesRead = it } != -1) {
                    val actualData = buffer.copyOfRange(0, bytesRead)
                    val processedChunk = cipher.doFinal(actualData)
                    output.write(processedChunk)
                }
            } ?: throw Exception("Unable to open output stream")
        } ?: throw Exception("Unable to open input stream")
    }

    private fun getPublicKey(): Key {
        val publicKeyString = publicKey.value ?: throw Exception("Public key is not available")
        val publicKeyBytes = Base64.getDecoder().decode(publicKeyString)
        val keySpec = X509EncodedKeySpec(publicKeyBytes)
        return KeyFactory.getInstance("RSA").generatePublic(keySpec)
    }

    private fun getPrivateKey(): Key {
        val privateKeyString = privateKey.value ?: throw Exception("Private key is not available")
        val privateKeyBytes = Base64.getDecoder().decode(privateKeyString)
        val keySpec = PKCS8EncodedKeySpec(privateKeyBytes)
        return KeyFactory.getInstance("RSA").generatePrivate(keySpec)
    }

    fun encryptFile(inputUri: Uri, outputUri: Uri) {
        val publicKey = getPublicKey()
        val cipher = getCipherInstance(Cipher.ENCRYPT_MODE, publicKey)
        processFile(
            inputUri,
            outputUri,
            cipher,
            245
        ) // RSA can handle up to 245 bytes for 2048-bit keys
    }

    fun decryptFile(inputUri: Uri, outputUri: Uri) {
        val privateKey = getPrivateKey()
        val cipher = getCipherInstance(Cipher.DECRYPT_MODE, privateKey)
        processFile(inputUri, outputUri, cipher, 256) // Encrypted block size for 2048-bit keys
    }
}
