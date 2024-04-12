package com.practicum.playlistmaker.mediaLibrary.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.practicum.playlistmaker.databinding.ActivityMediaLibraryBinding

class MediaLibraryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMediaLibraryBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}