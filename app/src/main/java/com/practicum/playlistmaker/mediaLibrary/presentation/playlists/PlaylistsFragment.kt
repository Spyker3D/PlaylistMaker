package com.practicum.playlistmaker.mediaLibrary.presentation.playlists

import android.os.Bundle
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentPlaylistsBinding
import com.practicum.playlistmaker.mediaLibrary.domain.entities.Playlist
import org.koin.androidx.viewmodel.ext.android.viewModel

private const val PLAYLIST_CLICK_DEBOUNCE_DELAY = 200L

class PlaylistsFragment : Fragment() {

    private var _binding: FragmentPlaylistsBinding? = null
    private val binding get() = _binding!!
    private var isClickAllowed = true
    private val viewModel: PlaylistsViewModel by viewModel<PlaylistsViewModel>()
    lateinit var playlistsAdapter: PlaylistAdapter
    private var lastTimeClicked: Long = 0L

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentPlaylistsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        isClickAllowed = true

        binding.playlistsGridLayout.layoutManager = GridLayoutManager(requireContext(), 2)
        playlistsAdapter = PlaylistAdapter(emptyList(), onPlaylistListener = {
            openPlaylistScreen(it)
        })

        binding.playlistsGridLayout.adapter = playlistsAdapter

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
            val bundle = bundleOf(PLAYLIST_NAME_KEY to playlist.playlistName)
            findNavController().navigate(
                R.id.action_mediaLibraryFragment_to_playlistDetailsAndEditFragment,
                bundle
            )
        }
    }

    private fun clickDebounce(): Boolean {
        val currentTime = SystemClock.uptimeMillis()
        if (currentTime - lastTimeClicked < PLAYLIST_CLICK_DEBOUNCE_DELAY) return false

        lastTimeClicked = currentTime
        return true
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
        const val PLAYLIST_NAME_KEY = "playlist_name"
    }
}




