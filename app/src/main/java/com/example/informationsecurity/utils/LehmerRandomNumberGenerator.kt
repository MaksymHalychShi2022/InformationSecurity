package com.example.informationsecurity.utils

class LehmerRandomNumberGenerator(
    private var state: Long = 1,
    private val a: Long = 48271,
    private val c: Long = 12341,
    private val m: Long = 2147483647
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
}
