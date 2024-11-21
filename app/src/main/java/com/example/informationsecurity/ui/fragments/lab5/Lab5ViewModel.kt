package com.example.informationsecurity.ui.fragments.lab5

import android.app.Application
import android.content.ContentResolver
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.informationsecurity.utils.OperationState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.security.KeyFactory
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.Signature
import java.security.spec.InvalidKeySpecException
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import java.util.Base64

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
                _signatureOutput.postValue(digitalSignatureEncoded)

                operationState.postValue(OperationState.Success(Unit))
            } catch (e: IllegalArgumentException) {
                operationState.postValue(OperationState.Error("Invalid input: ${e.message}"))
            } catch (e: InvalidKeySpecException) {
                operationState.postValue(OperationState.Error("Invalid private key format: ${e.message}"))
            } catch (e: Exception) {
                operationState.postValue(OperationState.Error("Error signing input: ${e.message}"))
            }
        }

        return operationState
    }


    fun sign(uri: Uri): LiveData<OperationState<Unit>> {
        val operationState = MutableLiveData<OperationState<Unit>>()

        viewModelScope.launch {
            operationState.postValue(OperationState.Loading())
            try {
                // Ensure keys are available
                val privateKeyEncoded = _privateKey.value
                if (privateKeyEncoded.isNullOrEmpty()) {
                    throw Exception("Private key not available")
                }

                // Decode the private key from Base64
                val privateKeyBytes = Base64.getDecoder().decode(privateKeyEncoded)
                val privateKey = java.security.KeyFactory.getInstance("DSA")
                    .generatePrivate(java.security.spec.PKCS8EncodedKeySpec(privateKeyBytes))

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
                _signatureOutput.postValue(digitalSignatureEncoded)

                operationState.postValue(OperationState.Success(Unit))
            } catch (e: Exception) {
                operationState.postValue(OperationState.Error("Error signing file: ${e.message}"))
            }
        }

        return operationState
    }

    fun saveSignatureOutput(uri: Uri): LiveData<OperationState<Unit>> {
        val operationState = MutableLiveData<OperationState<Unit>>()

        viewModelScope.launch {
            operationState.postValue(OperationState.Loading())
            try {
                val signatureContent =
                    _signatureOutput.value ?: throw Exception("Signature output is not available")
                val contentResolver = getApplication<Application>().contentResolver
                val outputStream = contentResolver.openOutputStream(uri)

                outputStream?.use {
                    it.bufferedWriter().use { writer -> writer.write(signatureContent) }
                    operationState.postValue(OperationState.Success(Unit))
                } ?: throw Exception("Unable to open OutputStream")

            } catch (e: Exception) {
                operationState.postValue(OperationState.Error("Error saving signature output: ${e.message}"))
            }
        }

        return operationState
    }

    fun verifySignature(inputData: String): LiveData<OperationState<Boolean>> {
        val operationState = MutableLiveData<OperationState<Boolean>>()

        viewModelScope.launch {
            operationState.postValue(OperationState.Loading())
            try {
                // Ensure the public key and signature are available
                val publicKeyEncoded = _publicKey.value
                val storedSignature = _signatureOutput.value

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
                val isVerified = signature.verify(digitalSignature)

                operationState.postValue(OperationState.Success(isVerified))
            } catch (e: Exception) {
                operationState.postValue(OperationState.Error("Error verifying signature: ${e.message}"))
            }
        }

        return operationState
    }


    fun verifyFileSignature(uri: Uri): LiveData<OperationState<Boolean>> {
        val operationState = MutableLiveData<OperationState<Boolean>>()

        viewModelScope.launch {
            operationState.postValue(OperationState.Loading())
            try {
                // Ensure the public key and signature are available
                val publicKeyEncoded = _publicKey.value
                val storedSignature = _signatureOutput.value

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
                val isVerified = signature.verify(digitalSignature)

                operationState.postValue(OperationState.Success(isVerified))
            } catch (e: Exception) {
                operationState.postValue(OperationState.Error("Error verifying signature: ${e.message}"))
            }
        }

        return operationState
    }

    fun loadSignature(uri: Uri): LiveData<OperationState<Unit>> {
        val operationState = MutableLiveData<OperationState<Unit>>()

        viewModelScope.launch {
            operationState.postValue(OperationState.Loading())
            try {
                // Open an InputStream to read the content from the Uri
                val contentResolver = getApplication<Application>().contentResolver
                val inputStream = contentResolver.openInputStream(uri)

                inputStream?.use {
                    val content = it.bufferedReader().use { reader -> reader.readText() }
                    _signatureOutput.postValue(content) // Save the read content to signatureOutput LiveData
                    operationState.postValue(OperationState.Success(Unit))
                } ?: throw Exception("Unable to open InputStream")

            } catch (e: Exception) {
                operationState.postValue(OperationState.Error("Error loading signature: ${e.message}"))
            }
        }

        return operationState
    }


}
