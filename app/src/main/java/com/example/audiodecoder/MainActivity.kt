package com.example.audiodecoder

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.audiodecoder.databinding.ActivityMainBinding
import java.io.InputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val context: Context = this

        val audioFile = context.resources.openRawResource(R.raw.file_2)
        val audioData = extractAudioData(audioFile)
        val humanReadableForm = convertToHumanReadableForm(audioData)
        Log.d("Readable","${humanReadableForm.count()}")

        //app will crash here
        //binding.textView.text = humanReadableForm
    }



    fun extractAudioData(fileInputStream: InputStream): FloatArray {
        val data = mutableListOf<Float>()
        val chunkSize = 4 + (8 + 16) * 2
        val byteArray = ByteArray(chunkSize)

        fileInputStream.read(byteArray, 0, chunkSize)
        val buffer = ByteBuffer.wrap(byteArray)
        buffer.order(ByteOrder.LITTLE_ENDIAN)

        val bitsPerSample = buffer.getShort(34).toInt()
        val bytePerSample = bitsPerSample / 8
        fileInputStream.skip(chunkSize.toLong())

        while (true) {
            val sample = ByteArray(bytePerSample)
            val v = fileInputStream.read(sample, 0, bytePerSample)
            if (v == -1) break

            val sampleBuffer = ByteBuffer.wrap(sample)
            sampleBuffer.order(ByteOrder.LITTLE_ENDIAN)
            data.add(when (bitsPerSample) {
                8 -> sampleBuffer.get().toFloat() / 128f - 1f
                16 -> sampleBuffer.getShort().toFloat() / 32768f
                else -> 0f
            })
        }

        return data.toFloatArray()
    }

     fun convertToHumanReadableForm(data: FloatArray): String {
        val sb = StringBuilder()
        data.forEach {
            sb.append("${it.toInt()}\n")
        }
        return sb.toString()
    }

}
