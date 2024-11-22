package com.example.informationsecurity.ui.fragments.lab3

import android.app.Application
import android.content.ContentResolver
import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.informationsecurity.ui.fragments.BaseViewModel
import com.example.informationsecurity.utils.LehmerRandomNumberGenerator
import com.example.informationsecurity.utils.MD5
import com.example.informationsecurity.utils.RC5
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.bouncycastle.crypto.modes.CBCBlockCipher
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher
import org.bouncycastle.crypto.params.ParametersWithIV
import org.bouncycastle.crypto.params.RC5Parameters
import java.io.FileNotFoundException

class Lab3ViewModel(application: Application) : BaseViewModel(application) {
    private val rounds: Int = 12  // Use the rounds parameter
    private val keyLength: Int = 8 // 128-bit key length
    private val contentResolver: ContentResolver = getApplication<Application>().contentResolver

    private val _passphrase = MutableLiveData<String>()
    val passphrase: LiveData<String> = _passphrase

    fun updatePassphrase(str: String) {
        _passphrase.postValue(str)
    }

    // Generate MD5 hash from passphrase
    private fun generateKey(passphrase: String): ByteArray {
        val md5Digest = MD5()
        val hash = md5Digest.digest(passphrase.toByteArray(Charsets.UTF_8))
        return hash.copyOf(keyLength) // Truncate or pad to keyLength
    }

    private fun generateIV(): ByteArray {
        val iv = ByteArray(keyLength * 2) // RC5-64 uses an 8-byte block size
        val generator = LehmerRandomNumberGenerator()
        for (i in iv.indices) {
            // Generate the next random number and convert it to a byte (taking the least significant byte)
            iv[i] = (generator.next() and 0xFF).toByte()
        }
        return iv
    }


    private suspend fun processFile(
        inputUri: Uri, outputUri: Uri, encrypt: Boolean
    ) {
        withContext(Dispatchers.IO) {
            val outputFile = DocumentFile.fromSingleUri(getApplication(), outputUri)
            val key = generateKey(_passphrase.value ?: "")

            // Generate or retrieve IV based on encryption or decryption
            val iv: ByteArray = if (encrypt) {
                generateIV() // Generate a new IV for encryption
            } else {
                // Retrieve the IV from the beginning of the input stream during decryption
                val inputStream = contentResolver.openInputStream(inputUri)
                    ?: throw FileNotFoundException("Failed to open input stream for URI: $inputUri")

                val ivBuffer = ByteArray(keyLength * 2) // 8-byte IV for RC5-64
                inputStream.read(ivBuffer)
                inputStream.close()
                ivBuffer
            }

            if (iv.size != keyLength * 2) {
                throw IllegalArgumentException("IV must be 8 bytes long, but got ${iv.size} bytes")
            }
            if (key.size != keyLength) {
                throw IllegalArgumentException("key must be 8 bytes long, but got ${iv.size} bytes")
            }
            val cipher = PaddedBufferedBlockCipher(CBCBlockCipher(RC5()))
            val params = ParametersWithIV(RC5Parameters(key, rounds), iv)
            cipher.init(encrypt, params)

            // Open input and output streams using the provided URIs
            val inputStream = contentResolver.openInputStream(inputUri)
                ?: throw FileNotFoundException("Failed to open input stream for URI: $inputUri")

            val outputStream = contentResolver.openOutputStream(outputUri)
                ?: throw FileNotFoundException("Failed to open output stream for URI: $outputUri")

            // Encryption or decryption logic with IV handling
            inputStream.use { input ->
                outputStream.use { output ->
                    if (encrypt) {
                        // Write the IV to the output stream for later use in decryption
                        output.write(iv)
                    } else {
                        // Skip the first 8 bytes of the input stream (IV) for decryption
                        input.skip(keyLength.toLong() * 2)
                    }

                    val inputBuffer = ByteArray(1024)
                    val outputBuffer = ByteArray(cipher.getOutputSize(inputBuffer.size))
                    var bytesRead: Int

                    while (input.read(inputBuffer).also { bytesRead = it } != -1) {
                        val outputLength = cipher.processBytes(
                            inputBuffer, 0, bytesRead, outputBuffer, 0
                        )
                        if (outputLength > 0) {
                            output.write(outputBuffer, 0, outputLength)
                        }
                    }

                    // Write final bytes
                    val finalOutputLength = cipher.doFinal(outputBuffer, 0)
                    if (finalOutputLength > 0) {
                        output.write(outputBuffer, 0, finalOutputLength)
                    }
                }
            }
        }
    }


    suspend fun encryptFile(inputUri: Uri, outputUri: Uri) = processFile(inputUri, outputUri, true)

    suspend fun decryptFile(inputUri: Uri, outputUri: Uri) = processFile(inputUri, outputUri, false)
}
