package com.practicum.playlistmaker.mediaLibrary.presentation.favoritetracks

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentFavoriteTracksBinding
import com.practicum.playlistmaker.player.presentation.AudioplayerActivity
import com.practicum.playlistmaker.player.presentation.KEY_SELECTED_TRACK_DETAILS
import com.practicum.playlistmaker.search.presentation.entities.Track
import com.practicum.playlistmaker.search.ui.TrackAdapter
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

private const val TRACK_CLICK_DEBOUNCE_DELAY = 200L

class FavoriteTracksFragment : Fragment() {

    private var _binding: FragmentFavoriteTracksBinding? = null
    private val binding get() = _binding!!
    private val viewModel: FavoriteTracksViewModel by viewModel<FavoriteTracksViewModel>()
    private lateinit var favouriteTracksAdapter: TrackAdapter
    private var isClickAllowed = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentFavoriteTracksBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.favouriteTracksRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        favouriteTracksAdapter = TrackAdapter(emptyList(), onTrackClickListener = {
            openAudioPlayer(it)
        })
        binding.favouriteTracksRecyclerView.adapter = favouriteTracksAdapter

        viewModel.isFavouriteTracksListFilled.observe(viewLifecycleOwner) { render(it) }
    }

    override fun onResume() {
        super.onResume()
        viewModel.updateFavouriteList()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun render(favouriteTrackList: List<Track>) {
        favouriteTracksAdapter.updateList { favouriteTrackList }
        binding.imagePlaceholderFavoriteTracks.isVisible = favouriteTrackList.isEmpty()
        binding.favouriteTracksRecyclerView.isVisible = favouriteTrackList.isNotEmpty()
        binding.textviewFavouriteTracks.isVisible = favouriteTrackList.isEmpty()
    }

    private fun openAudioPlayer(track: Track) {
        if (clickDebounce()) {
            val audioplayerIntent =
                Intent(requireContext(), AudioplayerActivity::class.java)
            audioplayerIntent.putExtra(KEY_SELECTED_TRACK_DETAILS, track)
            startActivity(audioplayerIntent)
        }
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            viewLifecycleOwner.lifecycleScope.launch {
                delay(TRACK_CLICK_DEBOUNCE_DELAY)
                isClickAllowed = true
            }
        }
        return current
    }

    companion object {
        fun newInstance() = FavoriteTracksFragment()
    }
}
