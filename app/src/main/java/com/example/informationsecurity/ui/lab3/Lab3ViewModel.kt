package com.example.informationsecurity.ui.lab3

import android.app.Application
import android.content.ContentResolver
import android.net.Uri
import android.provider.OpenableColumns
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.informationsecurity.utils.OperationState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.bouncycastle.crypto.engines.RC564Engine
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher
import org.bouncycastle.crypto.params.RC5Parameters
import java.io.FileNotFoundException
import java.security.MessageDigest

class Lab3ViewModel(application: Application) : AndroidViewModel(application) {
    private val rounds: Int = 12  // Use the rounds parameter
    private val keyLength: Int = 16 // 128-bit key length
    private val contentResolver: ContentResolver = getApplication<Application>().contentResolver

    private val _passphrase = MutableLiveData<String>().apply {
        value = "This is the passphrase"
    }
    val passphrase: LiveData<String> = _passphrase

    // Generate MD5 hash from passphrase
    private fun generateKey(passphrase: String): ByteArray {
        val md5Digest = MessageDigest.getInstance("MD5")
        val hash = md5Digest.digest(passphrase.toByteArray(Charsets.UTF_8))
        return hash.copyOf(keyLength) // Truncate or pad to keyLength
    }

    private fun createOutputUri(inputUri: Uri, isEncryption: Boolean): Uri? {
        // Get a DocumentFile representing the inputUri
        val inputDocumentFile = DocumentFile.fromSingleUri(getApplication(), inputUri)
        val parentDocumentFile = inputDocumentFile?.parentFile

        // Check if the parent directory is available and writable
        if (parentDocumentFile == null || !parentDocumentFile.canWrite()) {
            return null
        }

        // Set the output file name based on the operation
        val outputFileName = if (isEncryption) {
            "${inputDocumentFile.name}.enc"
        } else {
            inputDocumentFile.name?.removeSuffix(".enc") ?: return null
        }

        // Create a new file in the same directory with the new name
        val outputDocumentFile = parentDocumentFile.createFile(
            "application/octet-stream", // MIME type for binary data
            outputFileName
        )

        return outputDocumentFile?.uri
    }


    private fun processFile(
        inputUri: Uri,
        outputUri: Uri,
        encrypt: Boolean
    ): LiveData<OperationState<Unit>> {
        val operationState = MutableLiveData<OperationState<Unit>>()
        viewModelScope.launch {
            operationState.postValue(OperationState.Loading())

            withContext(Dispatchers.IO) {
                try {
                    val key = generateKey(_passphrase.value ?: "")
                    val rc5Cipher = PaddedBufferedBlockCipher(RC564Engine())
                    val rc5Params = RC5Parameters(key, rounds)
                    rc5Cipher.init(encrypt, rc5Params)

                    // Open input and output streams using the provided URIs
                    val inputStream = contentResolver.openInputStream(inputUri)
                        ?: throw FileNotFoundException("Failed to open input stream for URI: $inputUri")

                    val outputStream = contentResolver.openOutputStream(outputUri)
                        ?: throw FileNotFoundException("Failed to open output stream for URI: $outputUri")

                    inputStream.use { input ->
                        outputStream.use { output ->
                            val buffer = ByteArray(1024)
                            val outputBuffer = ByteArray(rc5Cipher.getOutputSize(buffer.size))
                            var bytesRead: Int

                            while (input.read(buffer).also { bytesRead = it } != -1) {
                                val outputLength = rc5Cipher.processBytes(buffer, 0, bytesRead, outputBuffer, 0)
                                output.write(outputBuffer, 0, outputLength)
                            }
                            val finalOutputLength = rc5Cipher.doFinal(outputBuffer, 0)
                            output.write(outputBuffer, 0, finalOutputLength)
                        }
                    }

                    operationState.postValue(OperationState.Success(Unit))
                } catch (e: FileNotFoundException) {
                    e.printStackTrace()
                    operationState.postValue(OperationState.Error("File not found: ${e.message}"))
                } catch (e: Exception) {
                    e.printStackTrace()
                    operationState.postValue(OperationState.Error("Error: ${e.message}"))
                }
            }
        }
        return operationState
    }

    // Example usage for encryption
    fun encryptFile(inputUri: Uri, outputUri: Uri): LiveData<OperationState<Unit>> {
        return processFile(inputUri, outputUri, true)
    }

    // Example usage for decryption
    fun decryptFile(inputUri: Uri, outputUri: Uri): LiveData<OperationState<Unit>> {
        return processFile(inputUri, outputUri, false)
    }


    fun getFileName(uri: Uri): String? {
        var fileName: String? = null
        val cursor = getApplication<Application>().contentResolver.query(
            uri,
            arrayOf(OpenableColumns.DISPLAY_NAME),
            null,
            null,
            null
        )

        cursor?.use {
            if (it.moveToFirst()) {
                fileName = it.getString(it.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME))
            }
        }
        return fileName
    }
}
