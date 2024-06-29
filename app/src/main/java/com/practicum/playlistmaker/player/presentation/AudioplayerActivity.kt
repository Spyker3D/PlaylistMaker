package com.practicum.playlistmaker.player.presentation

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ActivityAudioplayerBinding
import com.practicum.playlistmaker.mediaLibrary.presentation.newplaylist.NewPlaylistFragment
import com.practicum.playlistmaker.mediaLibrary.presentation.playlists.PlaylistAdapter
import com.practicum.playlistmaker.mediaLibrary.presentation.playlists.PlaylistsState
import com.practicum.playlistmaker.search.presentation.entities.Track
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

const val KEY_SELECTED_TRACK_DETAILS = "TRACK_DETAILS"

class AudioplayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAudioplayerBinding
    lateinit var playlistsAdapter: BottomSheetAdapter

    private val viewModel: PlayerViewModel by viewModel<PlayerViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAudioplayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        playlistsAdapter = BottomSheetAdapter(emptyList(), onPlaylistListener = {
            viewModel.addTrackToPlaylist(it)
        })
        binding.bottomSheetRecyclerView.layoutManager = LinearLayoutManager(this)

        binding.bottomSheetRecyclerView.adapter = playlistsAdapter

        val bottomSheetBehavior =
            BottomSheetBehavior.from(binding.playlistBottomSheetContainer).apply {
                state = BottomSheetBehavior.STATE_HIDDEN
            }

        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {

            override fun onStateChanged(bottomSheet: View, newState: Int) {

                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> binding.overlay.isVisible = false
                    else -> binding.overlay.isVisible = true
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                binding.overlay.alpha = slideOffset + 1
            }
        })

        binding.arrowBackAudioplayer.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        viewModel.progressTimeLiveData.observe(this) {
            binding.replayProgress.text = it
        }

        viewModel.playerState.observe(this) {

            showTrackDetails(it.track)

            binding.playPauseButton.isEnabled = it !is ActivityPlayerState.Idle

            when (it) {
                is ActivityPlayerState.Idle -> setPlayerStatusButtonImagePlaceholder(R.drawable.button_play)
                is ActivityPlayerState.Playing -> setPlayerStatusButtonImagePlaceholder(R.drawable.pause_button)
                is ActivityPlayerState.Paused -> setPlayerStatusButtonImagePlaceholder(R.drawable.button_play)
            }
        }

        viewModel.isFavourite.observe(this) {
            if (it) {
                setFavouriteButtonImagePlaceholder(R.drawable.button_like_enabled)
            } else {
                setFavouriteButtonImagePlaceholder(R.drawable.button_like_disabled)
            }
        }

        viewModel.preparePlayer()

        binding.playPauseButton.setOnClickListener { viewModel.startOrPause() }
        binding.likeButton.setOnClickListener { viewModel.onFavouriteClicked() }
        binding.buttonNewPlaylistBottomSheet.setOnClickListener {

            viewModel.pausePlayer()
            if (savedInstanceState == null) {
                supportFragmentManager.beginTransaction().add(
                    R.id.audioplayer_fragment_container,
                    NewPlaylistFragment.newInstance(),
                    "NewPlaylistFragment"
                )
                    .commit()
            }

        }

        binding.addToPlayListButton.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }
        viewModel.playlistsState.observe(this) { render(it) }

        viewModel.trackAddToastState.observe(this) { toastMessage ->
            makeToast(toastMessage)
        }

    }

    private fun render(playlistsState: PlaylistsState) {
        when (playlistsState) {
            is PlaylistsState.Empty -> binding.bottomSheetRecyclerView.isVisible = false
            is PlaylistsState.Content -> {
                binding.bottomSheetRecyclerView.isVisible = true
                playlistsAdapter.updateList { playlistsState.playlistsList }
            }
        }

    }

    override fun onPause() {
        super.onPause()
        viewModel.pausePlayer()
    }

    private fun setPlayerStatusButtonImagePlaceholder(image: Int) {
        binding.playPauseButton.setImageResource(image)
    }

    private fun setFavouriteButtonImagePlaceholder(image: Int) {
        binding.likeButton.setImageResource(image)
    }

    private fun showTrackDetails(selectedTrack: Track) {
        Glide.with(this)
            .load(selectedTrack.artworkUrlLarge)
            .placeholder(R.drawable.placeholder_album)
            .centerCrop()
            .transform(RoundedCorners(this.resources.getDimensionPixelSize(R.dimen.track_image_rounding_audiotplayer)))
            .into(binding.albumImage)
        binding.trackName.text = selectedTrack.trackName
        binding.bandName.text = selectedTrack.artistName
        binding.albumNamePlaceholder.text = selectedTrack.collectionName
        binding.lengthPlaceholder.text = selectedTrack.trackTimeMillisFormatted
        binding.yearPlaceholder.text = selectedTrack.releaseYear
        binding.genreNamePlaceholder.text = selectedTrack.primaryGenreName
        binding.countryNamePlaceholder.text = selectedTrack.country
    }

    private fun makeToast(toastMessage: String) {
        Toast.makeText(this, toastMessage, Toast.LENGTH_LONG).show()
    }
}