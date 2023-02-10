package com.example.audiodecoder

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.nio.ByteBuffer
import java.nio.ByteOrder

class ActivityViewModelTest{

    //class under test
    private lateinit var activityViewModel: ActivityViewModel

    val bytes = byteArrayOf(0x01, 0x02, 0x03, 0x04)
    val numOfBytes = 4

    @Test
    fun `test byteArrayToNumber with BIG_ENDIAN order`() {
        val type = 0
        val expectedByteBuffer = ByteBuffer.allocate(4).apply {
            order(ByteOrder.BIG_ENDIAN)
            put(bytes)
            rewind()
        }

        val result = activityViewModel.byteArrayToNumber(bytes, numOfBytes, type)

        assertEquals(expectedByteBuffer, result)
    }

    @Test
    fun `test byteArrayToNumber with LITTLE_ENDIAN order`() {

        val type = 1
        val expectedByteBuffer = ByteBuffer.allocate(4).apply {
            order(ByteOrder.LITTLE_ENDIAN)
            put(bytes)
            rewind()
        }

        val result = activityViewModel.byteArrayToNumber(bytes, numOfBytes, type)

        assertEquals(expectedByteBuffer, result)
    }

    @Test
    fun `verify if convertToFloat returns a Float`() {
        val testArray = byteArrayOf(0, 1)
        val result = activityViewModel.convertToFloat(testArray, 1)

        assertTrue(result is Float)
    }
}