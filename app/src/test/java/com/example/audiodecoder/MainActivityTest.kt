package com.example.audiodecoder

import org.junit.Assert.assertEquals
import org.junit.Test


class MainActivityTest {
    @Test
    fun testConvertToHumanReadableForm() {
        // Test input data
        val inputData = floatArrayOf(-1.0f, -0.5f, 0.0f, 0.5f, 1.0f)

        // Expected output
        val expectedOutput = "-1\n-0\n0\n0\n1\n"

        // Call the function
        val output = MainActivity().convertToHumanReadableForm(inputData)

        // Assert the output is as expected
        assertEquals(expectedOutput, output)
    }

}