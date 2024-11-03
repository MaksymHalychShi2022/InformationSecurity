package com.example.informationsecurity

import com.example.informationsecurity.utils.RC5
import org.bouncycastle.crypto.CipherParameters
import org.bouncycastle.crypto.params.KeyParameter
import org.bouncycastle.crypto.params.RC5Parameters
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class RC5Test {

    private lateinit var cipher: RC5

    @Before
    fun setUp() {
        cipher = RC5()
    }

    @Test
    fun `test algorithm name`() {
        assertEquals("RC5", cipher.algorithmName)
    }

    @Test(expected = IllegalStateException::class)
    fun `test process block before init throws exception`() {
        val input = ByteArray(16) { 0 }
        val output = ByteArray(16)
        cipher.processBlock(input, 0, output, 0)
    }

    @Test
    fun `test init with KeyParameter`() {
        val key = ByteArray(16) { it.toByte() }
        val params = KeyParameter(key)
        cipher.init(true, params)

        val input = ByteArray(16) { it.toByte() }
        val output = ByteArray(16)

        val processedBlockSize = cipher.processBlock(input, 0, output, 0)

        assertEquals(16, processedBlockSize)
    }

    @Test
    fun `test init with RC5Parameters`() {
        val key = ByteArray(16) { it.toByte() }
        val params = RC5Parameters(key, 16)
        cipher.init(true, params)

        val input = ByteArray(16) { it.toByte() }
        val output = ByteArray(16)

        val processedBlockSize = cipher.processBlock(input, 0, output, 0)

        assertEquals(16, processedBlockSize)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `test init with invalid parameters throws exception`() {
        val params = object : CipherParameters {}
        cipher.init(true, params)
    }

    @Test
    fun `test encryption and decryption consistency`() {
        val key = ByteArray(16) { it.toByte() }
        val params = KeyParameter(key)
        cipher.init(true, params)

        val input = ByteArray(16) { it.toByte() }
        val encryptedOutput = ByteArray(16)
        cipher.processBlock(input, 0, encryptedOutput, 0)

        cipher.init(false, params)
        val decryptedOutput = ByteArray(16)
        cipher.processBlock(encryptedOutput, 0, decryptedOutput, 0)

        assertArrayEquals(input, decryptedOutput)
    }
}
