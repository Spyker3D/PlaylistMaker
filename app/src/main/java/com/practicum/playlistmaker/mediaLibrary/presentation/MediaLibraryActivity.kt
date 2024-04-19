package com.practicum.playlistmaker.mediaLibrary.presentation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.tabs.TabLayoutMediator
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ActivityMediaLibraryBinding

class MediaLibraryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMediaLibraryBinding
    private lateinit var tabMediator: TabLayoutMediator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMediaLibraryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbarMediaLibrary.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }  // почему не работает?

        binding.viewpagerMedialibrary.adapter =
            MediaLibraryViewPagerAdapter(supportFragmentManager, lifecycle)

        tabMediator = TabLayoutMediator(
            binding.tablayoutMedialibrary,
            binding.viewpagerMedialibrary
        ) { tab, position ->
            when (position) {
                0 -> tab.text = getString(R.string.medialibrary_tablayout_tab_1)
                1 -> tab.text = getString(R.string.medialibrary_tablayout_tab_2)
            }
        }

        tabMediator.attach()
    }

    override fun onDestroy() {
        super.onDestroy()
        tabMediator.detach()
    }
}