package com.example.audiodecoder

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.audiodecoder.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val activityViewModel = ActivityViewModel()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val context: Context = this

        val audioFile = context.resources.openRawResource(R.raw.file_1)
        activityViewModel.readingAudioFile(audioFile)

        initObserver()
    }

    private fun initObserver() {
        activityViewModel.audioData.observe(this) {
            binding.textView.text = it.toString()
        }
    }

}
