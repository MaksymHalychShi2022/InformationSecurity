package com.example.informationsecurity

import com.example.informationsecurity.utils.MD5
import org.junit.Assert.assertArrayEquals
import org.junit.Before
import org.junit.Test
import java.security.MessageDigest

class MD5Test {

    private lateinit var md5: MD5

    @Before
    fun setUp() {
        md5 = MD5()
    }

    @Test
    fun testDigest_withSimpleInput() {
        val input = "hello".toByteArray()
        val expected = MessageDigest.getInstance("MD5").digest(input)

        val result = md5.digest(input)

        assertArrayEquals(
            "The computed MD5 digest should match the expected value",
            expected,
            result
        )
    }

    @Test
    fun testDigest_withEmptyInput() {
        val input = ByteArray(0)
        val expected = MessageDigest.getInstance("MD5").digest(input)

        val result = md5.digest(input)

        assertArrayEquals(
            "The MD5 digest of an empty array should match the expected value",
            expected,
            result
        )
    }

    @Test
    fun testDigest_withLongInput() {
        val input =
            "This is a longer string used for testing the MD5 hashing implementation in the test case.".toByteArray()
        val expected = MessageDigest.getInstance("MD5").digest(input)

        val result = md5.digest(input)

        assertArrayEquals(
            "The computed MD5 digest of a long string should match the expected value",
            expected,
            result
        )
    }

    @Test
    fun testUpdate_andDigest() {
        val input1 = "part1".toByteArray()
        val input2 = "part2".toByteArray()

        md5.update(input1)
        md5.update(input2)
        val expected = MessageDigest.getInstance("MD5").apply {
            update(input1)
            update(input2)
        }.digest()

        val result = md5.digest()

        assertArrayEquals(
            "The MD5 digest after multiple updates should match the expected value",
            expected,
            result
        )
    }
}
