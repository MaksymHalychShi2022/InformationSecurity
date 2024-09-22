package com.example.informationsecurity.utils

import kotlin.math.sqrt

object RandomNumbersUtils {

    fun gcd(x: Long, y: Long): Long {
        return if (y == 0L) x else gcd(y, x % y)
    }

    // Function to estimate Pi using Cesaro's theorem
    fun estimatePi(randomNumberGenerator: () -> Long, totalPairs: Long = 1000L): Double {
        var coprimeCount = 0

        // Run the Cesaro test by generating pairs of random numbers
        for (i in 1..totalPairs) {
            val x = randomNumberGenerator()
            val y = randomNumberGenerator()

            // Check if the numbers are coprime
            if (gcd(x, y) == 1L) {
                coprimeCount++
            }
        }

        // Calculate the probability of two numbers being coprime
        val probabilityCoprime = coprimeCount.toDouble() / totalPairs

        // Use Cesaro's theorem to estimate Pi
        return sqrt(6.0 / probabilityCoprime)
    }
}