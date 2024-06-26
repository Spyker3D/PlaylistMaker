package com.practicum.playlistmaker.mediaLibrary.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayoutMediator
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentMediaLibraryBinding

class MediaLibraryFragment : Fragment() {

    private var _binding: FragmentMediaLibraryBinding? = null
    private val binding get() = _binding!!
    private lateinit var tabMediator: TabLayoutMediator

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMediaLibraryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.viewpagerMedialibrary.adapter =
            MediaLibraryViewPagerAdapter(childFragmentManager, lifecycle)

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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}