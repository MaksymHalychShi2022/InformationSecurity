package com.example.informationsecurity

import com.example.informationsecurity.utils.LehmerRandomNumberGenerator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class LehmerRandomNumberGeneratorTest {

    private lateinit var rng: LehmerRandomNumberGenerator

    @Before
    fun setUp() {
        rng = LehmerRandomNumberGenerator()
    }

    @Test
    fun testNext_generatesWithinBounds() {
        val result = rng.next()
        assertTrue("Generated number should be within the range [0, m-1]", result in 0 until 32767)
    }

    @Test
    fun testGenerateSequence_correctLength() {
        val n = 10L
        val sequence = rng.generateSequence(n)
        assertEquals("Generated sequence should have the correct length", n, sequence.size.toLong())
    }

    @Test
    fun testGetPeriod_findsPeriod() {
        val period = rng.getPeriod()
        assertTrue("The period should be greater than 0", period > 0)
    }
}
