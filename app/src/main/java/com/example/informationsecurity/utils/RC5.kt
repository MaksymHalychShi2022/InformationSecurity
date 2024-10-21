package com.example.informationsecurity.utils

import org.bouncycastle.crypto.BlockCipher
import org.bouncycastle.crypto.CipherParameters
import org.bouncycastle.crypto.params.KeyParameter
import org.bouncycastle.crypto.params.RC5Parameters

class RC5 : BlockCipher {
    companion object {
        private const val W = 64 // Word size in bits
        private const val R_DEFAULT = 12 // Default number of rounds
        private const val BLOCK_SIZE = 16 // 128-bit block size (two 64-bit words)
    }

    private var forEncryption = true
    private var S: LongArray? = null
    private var rounds = R_DEFAULT

    override fun init(forEncryption: Boolean, params: CipherParameters) {
        this.forEncryption = forEncryption

        if (params is KeyParameter) {
            val key = params.key
            // Initialize RC5 subkeys
            S = LongArray(2 * (rounds + 1))
            rc5KeySetup(key, S!!)
        } else if (params is RC5Parameters) {
            val key = params.key
            this.rounds = params.rounds
            // Initialize RC5 subkeys
            S = LongArray(2 * (rounds + 1))
            rc5KeySetup(key, S!!)
        } else {
            throw IllegalArgumentException("Parameters must be of type KeyParameter or RC5Parameters")
        }
    }

    override fun getAlgorithmName(): String {
        return "RC5"
    }

    override fun getBlockSize(): Int {
        return BLOCK_SIZE
    }

    override fun processBlock(input: ByteArray, inOff: Int, output: ByteArray, outOff: Int): Int {
        val S = S ?: throw IllegalStateException("RC5 not initialized")

        // Read input block as two 64-bit words
        var A = bytesToLong(input, inOff)
        var B = bytesToLong(input, inOff + 8)

        // Encrypt or decrypt the block based on the mode
        if (forEncryption) {
            val encrypted = rc5Encrypt(A, B)
            A = encrypted.first
            B = encrypted.second
        } else {
            val decrypted = rc5Decrypt(A, B)
            A = decrypted.first
            B = decrypted.second
        }

        // Write output block
        longToBytes(A, output, outOff)
        longToBytes(B, output, outOff + 8)

        return BLOCK_SIZE
    }

    override fun reset() {
        // No additional state to reset
    }

    // Rotate left
    private fun rotl(x: Long, y: Int): Long {
        return (x shl y) or (x ushr (W - y))
    }

    // Rotate right
    private fun rotr(x: Long, y: Int): Long {
        return (x ushr y) or (x shl (W - y))
    }

    // Key expansion for RC5
    private fun rc5KeySetup(key: ByteArray, S: LongArray) {
        val C = key.size / 8
        val L = LongArray(C)

        // Convert the key bytes to key words
        for (i in key.indices) {
            L[i / 8] = (L[i / 8] shl 8) + (key[i].toLong() and 0xFF)
        }

        // Initialize the S array with P and Q
        S[0] = -0x61c88647fdbaee00 // Magic constant P
        for (i in 1 until 2 * (rounds + 1)) {
            S[i] = S[i - 1] + -0x359d3e2a4b1c6e00 // Magic constant Q
        }

        var A = 0L
        var B = 0L
        var i = 0
        var j = 0

        // Key mixing loop
        for (k in 0 until 3 * maxOf(2 * (rounds + 1), C)) {
            A = S[i] + A + B
            A = rotl(A, 3)
            S[i] = A

            B = L[j] + A + B
            B = rotl(B, ((A + B) % W).toInt())
            L[j] = B

            i = (i + 1) % (2 * (rounds + 1))
            j = (j + 1) % C
        }
    }

    // RC5 encryption
    private fun rc5Encrypt(A: Long, B: Long): Pair<Long, Long> {
        var A = A + S!![0]
        var B = B + S!![1]

        for (i in 1..rounds) {
            A = rotl(A xor B, (B % W).toInt()) + S!![2 * i]
            B = rotl(B xor A, (A % W).toInt()) + S!![2 * i + 1]
        }

        return Pair(A, B)
    }

    // RC5 decryption
    private fun rc5Decrypt(A: Long, B: Long): Pair<Long, Long> {
        var A = A
        var B = B

        for (i in rounds downTo 1) {
            B = rotr(B - S!![2 * i + 1], (A % W).toInt()) xor A
            A = rotr(A - S!![2 * i], (B % W).toInt()) xor B
        }

        B -= S!![1]
        A -= S!![0]

        return Pair(A, B)
    }

    // Convert 8 bytes to a long
    private fun bytesToLong(b: ByteArray, offset: Int): Long {
        return (b[offset].toLong() and 0xFF) or
                ((b[offset + 1].toLong() and 0xFF) shl 8) or
                ((b[offset + 2].toLong() and 0xFF) shl 16) or
                ((b[offset + 3].toLong() and 0xFF) shl 24) or
                ((b[offset + 4].toLong() and 0xFF) shl 32) or
                ((b[offset + 5].toLong() and 0xFF) shl 40) or
                ((b[offset + 6].toLong() and 0xFF) shl 48) or
                ((b[offset + 7].toLong() and 0xFF) shl 56)
    }

    // Convert a long to 8 bytes
    private fun longToBytes(x: Long, b: ByteArray, offset: Int) {
        for (i in 0 until 8) {
            b[offset + i] = ((x shr (i * 8)) and 0xFF).toByte()
        }
    }
}


