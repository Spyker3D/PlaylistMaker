package com.practicum.playlistmaker.player.presentation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ActivityAudioplayerBinding
import com.practicum.playlistmaker.player.domain.entities.AudioPlayerStatesListener
import com.practicum.playlistmaker.search.presentation.entities.Track
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.Locale

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

        viewModel.isPlaying.observe(this) {
            showTrackDetails(it.track)
            when (it) {
                is ActivityPlayerState.Idle -> setImagePlaceholder(R.drawable.button_play)
                is ActivityPlayerState.IsPlaying -> setImagePlaceholder(R.drawable.pause_button)
                is ActivityPlayerState.IsPaused -> setImagePlaceholder(R.drawable.button_play)
            }
        }

        viewModel.preparePlayer()

        binding.playStatusPlaceholder.setOnClickListener { viewModel.startOrPause() }
    }

    override fun onPause() {
        super.onPause()
        viewModel.pausePlayer()
    }

    private fun setImagePlaceholder(image: Int) {
        binding.playStatusPlaceholder.setImageResource(image)
    }

    private fun showTrackDetails(selectedTrack: Track) {
        binding.trackName.text = selectedTrack.trackName
        binding.bandName.text = selectedTrack.artistName
        binding.albumNamePlaceholder.text = selectedTrack.collectionName
        binding.lengthPlaceholder.text = selectedTrack.trackTimeMillisFormatted
        binding.yearPlaceholder.text = selectedTrack.releaseYear
        binding.genreNamePlaceholder.text = selectedTrack.primaryGenreName
        binding.countryNamePlaceholder.text = selectedTrack.country
        Glide.with(this)
            .load(selectedTrack.artworkUrlLarge)
            .placeholder(R.drawable.placeholder_album)
            .centerCrop()
            .transform(RoundedCorners(this.resources.getDimensionPixelSize(R.dimen.track_image_rounding_audiotplayer)))
            .into(binding.albumImage)
    }
}