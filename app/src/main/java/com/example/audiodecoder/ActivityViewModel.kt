package com.example.audiodecoder

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.io.InputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder

class ActivityViewModel: ViewModel(){

    private val _audioData = MutableLiveData<FloatArray>()
    val audioData: LiveData<FloatArray> get() = _audioData


    val type = intArrayOf(0, 1, 0, 0, 1, 1, 1, 1, 1, 1, 1, 0, 1)
    val numberOfBytes = intArrayOf(4, 4, 4, 4, 4, 2, 2, 4, 4, 2, 2, 4, 4)
    var chunkSize = 0
    var subChunk1Size = 0
    var sampleRate = 0
    var byteRate = 0
    var subChunk2Size = 1
    var bytePerSample = 0
    var audioFormat = 0
    var numChannels = 0
    var blockAlign = 0
    var bitsPerSample = 8
    var chunkID = ""
    var format = ""
    var subChunk1ID = ""
    var subChunk2ID = ""

     fun byteArrayToNumber(bytes: ByteArray, numOfBytes: Int, type: Int): ByteBuffer {
        val buffer = ByteBuffer.allocate(numOfBytes)
        if (type == 0) {
            buffer.order(ByteOrder.BIG_ENDIAN)
        } else {
            buffer.order(ByteOrder.LITTLE_ENDIAN)
        }
        buffer.put(bytes)
        buffer.rewind()
        return buffer
    }

    fun convertToFloat(array: ByteArray, type: Int): Float {
        val buffer = ByteBuffer.wrap(array)
        if (type == 1) {
            buffer.order(ByteOrder.LITTLE_ENDIAN)
        }
        return buffer.getShort().toFloat()
    }

    fun readingAudioFile(fileInputStream: InputStream) {
        return try {
            var byteBuffer: ByteBuffer
            for (i in numberOfBytes.indices) {
                // Allocate a new byte array with the length of the number of bytes at the current index of the loop
                val byteArray = ByteArray(numberOfBytes[i])
                // Read the byteArray from the fileInputStream with the starting position at 0 and the number of bytes read equal to the number of bytes at the current index
                var r = fileInputStream.read(byteArray, 0, numberOfBytes[i])
                // Convert the byteArray to a number
                byteBuffer = byteArrayToNumber(byteArray, numberOfBytes[i], type[i])
                when (i) {
                    0 -> chunkID = String(byteArray)
                    1 -> chunkSize = byteBuffer.int
                    2 -> format = String(byteArray)
                    3 -> subChunk1ID = String(byteArray)
                    4 -> subChunk1Size = byteBuffer.int
                    5 -> audioFormat = byteBuffer.short.toInt()
                    6 -> numChannels = byteBuffer.short.toInt()
                    7 -> sampleRate = byteBuffer.int
                    8 -> byteRate = byteBuffer.int
                    9 -> blockAlign = byteBuffer.short.toInt()
                    10 -> bitsPerSample = byteBuffer.short.toInt()
                    11 -> {
                        subChunk2ID = String(byteArray)
                        if (subChunk2ID == "data") {
                            // skip the rest of the code block and go to the next iteration of the loop
                            continue
                        } else if (subChunk2ID == "LIST") {
                            val byteArray2 = ByteArray(4)
                            r = fileInputStream.read(byteArray2, 0, 4)
                            byteBuffer = byteArrayToNumber(byteArray2, 4, 1)
                            val temp = byteBuffer.int
                            val byteArray3 = ByteArray(temp)
                            r = fileInputStream.read(byteArray3, 0, temp)
                            r = fileInputStream.read(byteArray2, 0, 4)
                            subChunk2ID = String(byteArray2)
                        }
                    }
                    12 -> subChunk2Size = byteBuffer.int
                }
            }
            bytePerSample = bitsPerSample / 8
            val dataVector = mutableListOf<Float>()
            while (true) {
                val byteArray = ByteArray(bytePerSample)
                val v = fileInputStream.read(byteArray, 0, bytePerSample)
                val value = convertToFloat(byteArray, 1)
                dataVector.add(value)
                if (v == -1) break
            }
            val data = FloatArray(dataVector.size)
            for (i in dataVector.indices) {
                data[i] = dataVector[i]
            }
            Log.d("Data", "$data")
            _audioData.value = data
        } catch (e: Exception) {
            println("Error: $e")
        }
    }
}