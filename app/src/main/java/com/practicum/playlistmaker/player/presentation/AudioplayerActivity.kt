package com.practicum.playlistmaker.player.presentation

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ActivityAudioplayerBinding
import com.practicum.playlistmaker.search.presentation.entities.Track
import org.koin.androidx.viewmodel.ext.android.viewModel

const val KEY_SELECTED_TRACK_DETAILS = "TRACK_DETAILS"

class AudioplayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAudioplayerBinding

    private val viewModel: PlayerViewModel by viewModel<PlayerViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAudioplayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
            if(it) {
                setFavouriteButtonImagePlaceholder(R.drawable.button_like_enabled)
            } else {
                setFavouriteButtonImagePlaceholder(R.drawable.button_like_disabled)
            }
        }

        viewModel.preparePlayer()

        binding.playPauseButton.setOnClickListener { viewModel.startOrPause() }

        binding.likeButton.setOnClickListener { viewModel.onFavouriteClicked() }
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
}