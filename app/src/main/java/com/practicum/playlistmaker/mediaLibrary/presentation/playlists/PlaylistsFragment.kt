package com.practicum.playlistmaker.mediaLibrary.presentation.playlists

import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentPlaylistsBinding
import com.practicum.playlistmaker.mediaLibrary.domain.entities.Playlist
import com.practicum.playlistmaker.player.presentation.ActivityPlayerState
import com.practicum.playlistmaker.player.presentation.AudioplayerActivity
import com.practicum.playlistmaker.player.presentation.KEY_SELECTED_TRACK_DETAILS
import com.practicum.playlistmaker.player.presentation.PlayerViewModel
import com.practicum.playlistmaker.search.presentation.entities.SearchState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File

private const val PLAYLIST_CLICK_DEBOUNCE_DELAY = 200L

class PlaylistsFragment : Fragment() {

    private var _binding: FragmentPlaylistsBinding? = null
    private val binding get() = _binding!!
    private var isClickAllowed = true
    private val viewModel: PlaylistsViewModel by viewModel<PlaylistsViewModel>()
    lateinit var playlistsAdapter: PlaylistAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentPlaylistsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding.playlistsGridLayout.layoutManager = GridLayoutManager(requireContext(), 2)
        playlistsAdapter = PlaylistAdapter(emptyList(), onPlaylistListener = {
            openPlaylistScreen(it)
        })

        binding.playlistsGridLayout.adapter = playlistsAdapter

        viewModel.loadAllPlaylists()

        viewModel.playlistsState.observe(viewLifecycleOwner) { render(it) }

//        val navHostFragment =
//            parentFragmentManager.findFragmentById(R.id.mediaLibraryFragment) as NavHostFragment
//        val navController = navHostFragment.findNavController()

        binding.buttonNewPlaylist.setOnClickListener {
            findNavController().navigate(R.id.action_mediaLibraryFragment_to_newPlaylistFragment)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun openPlaylistScreen(playlist: Playlist) {
        if (clickDebounce()) {
//            findNavController().navigate(R.id.action_mediaLibraryFragment_to_newPlaylistFragment) добавить код по переходу на экран плейлиста в следующем спринте
        }
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            viewLifecycleOwner.lifecycleScope.launch {
                delay(PLAYLIST_CLICK_DEBOUNCE_DELAY)
                isClickAllowed = true
            }
        }
        return current
    }

    private fun render(playlistsState: PlaylistsState) {
        when (playlistsState) {
            is PlaylistsState.Empty -> showEmptyScreen()
            is PlaylistsState.Content -> showContent(playlistsState.playlistsList)
        }

    }

    private fun showEmptyScreen() {
        binding.imagePlaceholderFavoriteTracks.isVisible = true
        binding.textviewFavouriteTracks.isVisible = true
        binding.playlistsGridLayout.isVisible = false
        binding.textviewFavouriteTracks.text = getString(R.string.playlists_text_placeholder)
    }

    private fun showContent(playlistsList: List<Playlist>) {
        binding.imagePlaceholderFavoriteTracks.isVisible = false
        binding.textviewFavouriteTracks.isVisible = false
        binding.playlistsGridLayout.isVisible = true
        playlistsAdapter.updateList { playlistsList }
//        val playlistImagePath = File(requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES), albumName)

    }

    companion object {
        fun newInstance() = PlaylistsFragment()
    }
}




