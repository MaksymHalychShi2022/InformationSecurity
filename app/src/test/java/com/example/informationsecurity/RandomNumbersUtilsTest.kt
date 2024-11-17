package com.example.informationsecurity

import com.example.informationsecurity.utils.RandomNumbersUtils
import org.junit.Assert.assertEquals
import org.junit.Test

class RandomNumbersUtilsTest {

    @Test
    fun `gcd of two numbers`() {
        // Test cases for gcd
        assertEquals(6L, RandomNumbersUtils.gcd(54L, 24L)) // gcd(54, 24) = 6
        assertEquals(1L, RandomNumbersUtils.gcd(17L, 31L)) // gcd(17, 31) = 1
        assertEquals(12L, RandomNumbersUtils.gcd(36L, 48L)) // gcd(36, 48) = 12
        assertEquals(1L, RandomNumbersUtils.gcd(7L, 13L)) // gcd(7, 13) = 1
        assertEquals(0L, RandomNumbersUtils.gcd(0L, 0L)) // gcd(0, 0) is defined as 0
        assertEquals(5L, RandomNumbersUtils.gcd(0L, 5L)) // gcd(0, 5) = 5
        assertEquals(5L, RandomNumbersUtils.gcd(5L, 0L)) // gcd(5, 0) = 5
    }

    @Test
    fun `estimatePi with predefined random number generator`() {
        // Define a deterministic random number generator for test consistency
        val randomGenerator = { (1..100).random().toLong() }

        // Run the estimatePi function
        val estimatedPi = RandomNumbersUtils.estimatePi(randomGenerator, 10000L)

        // Assert the value is approximately close to the actual Pi (±0.1)
        assertEquals(3.14, estimatedPi, 0.1)
    }

    @Test
    fun `estimatePi with known coprime cases`() {
        // Define a deterministic random number generator that generates coprime pairs
        var counter = 0
        val randomGenerator = {
            counter++
            if (counter % 2 == 0) 9L else 16L // gcd(9, 16) = 1
        }

        // Run the estimatePi function
        val estimatedPi = RandomNumbersUtils.estimatePi(randomGenerator, 100L)

        // Assert that the estimated Pi is approximately close to the actual Pi (±1.0)
        assertEquals(3.14, estimatedPi, 1.0)
    }

    @Test
    fun `estimatePi with edge cases`() {
        // Edge case: Random generator always returns the same value
        val constantGenerator = { 10L }

        // Since the gcd of identical numbers is not 1, coprimeCount will be 0
        val estimatedPi = RandomNumbersUtils.estimatePi(constantGenerator, 100L)

        // Since no pairs are coprime, probabilityCoprime = 0 and the result should be Double.NaN
        assertEquals(Double.NaN, estimatedPi, 0.0)
    }

    @Test
    fun `gcd performance with large numbers`() {
        // Test with very large numbers
        val x = 9876543210L
        val y = 1234567890L

        // Expected result calculated manually
        val expectedGcd = 90L // gcd(9876543210, 1234567890) = 90

        // Assert gcd result
        assertEquals(expectedGcd, RandomNumbersUtils.gcd(x, y))
    }
}
