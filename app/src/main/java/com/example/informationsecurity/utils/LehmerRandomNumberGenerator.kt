package com.example.informationsecurity.utils

class LehmerRandomNumberGenerator(
    private var state: Long = 64, // 64
    private val a: Long = 8,  // 2^3
    private val c: Long = 8,  // 8
    val m: Long = 32767  // 2^15-1
) {

    // Lehmer Random Number Generator formula
    fun next(): Long {
        state = (a * state + c) % m
        return state
    }

    fun generateSequence(n: Long): List<Long> {
        val randomNumbers = mutableListOf<Long>()
        for (i in 1..n) {
            randomNumbers.add(next())
        }
        return randomNumbers
    }

    // Generate numbers until the first repeating number is found
    fun getPeriod(): Long {
        val initialState = state  // Remember the first state (seed)
        var count = 0

        while (true) {
            val number = next()
            count++

            // If we encounter the initial state again, the period is found
            if (number == initialState) {
                return count.toLong()  // The period is the number of iterations taken to repeat the initial state
            }
        }
    }
}
