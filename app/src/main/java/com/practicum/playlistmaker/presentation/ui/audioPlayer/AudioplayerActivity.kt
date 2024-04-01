package com.practicum.playlistmaker.presentation.ui.audioPlayer

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.creator.Creator
import com.practicum.playlistmaker.databinding.ActivityAudioplayerBinding
import com.practicum.playlistmaker.domain.entities.AudioPlayerStatesListener
import com.practicum.playlistmaker.domain.interactor.PlayerState
import com.practicum.playlistmaker.domain.entities.TrackInfo
import java.text.SimpleDateFormat
import java.util.Locale

const val KEY_SELECTED_TRACK_DETAILS = "TRACK_DETAILS"
const val UPDATE_PLAY_PROGRESS_DEBOUNCE_DELAY = 300L

class AudioplayerActivity : AppCompatActivity() {

    private lateinit var playStatusButton: ImageButton
    private lateinit var url: String
    private val handler = Handler(Looper.getMainLooper())
    private val replayProgressRunnable = object : Runnable {
        override fun run() {
            updateProgressTime()
            if (audioPlayerInteractor.audioPlayerState == PlayerState.STATE_PLAYING) {
                handler.postDelayed(this, UPDATE_PLAY_PROGRESS_DEBOUNCE_DELAY)
            }
        }
    }
    private lateinit var binding: ActivityAudioplayerBinding
    private val creator by lazy { Creator(applicationContext) }
    private val audioPlayerInteractor by lazy { creator.provideAudioPlayerInteractor() }
    private val dateFormat by lazy { SimpleDateFormat("m:ss", Locale.getDefault()) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAudioplayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.arrowBackAudioplayer.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        playStatusButton = binding.playStatusPlaceholder

        val track =
            intent.extras?.getParcelable(KEY_SELECTED_TRACK_DETAILS) as TrackInfo?

        binding.trackName.text = track?.trackName
        binding.bandName.text = track?.artistName
        binding.albumNamePlaceholder.text = track?.collectionName
        binding.lengthPlaceholder.text = track?.trackTimeMillisFormatted
        binding.yearPlaceholder.text = track?.releaseYear
        binding.genreNamePlaceholder.text = track?.primaryGenreName
        binding.countryNamePlaceholder.text = track?.country
        url = track?.previewUrl.toString()
        Glide.with(this)
            .load(track?.artworkUrlLarge)
            .placeholder(R.drawable.placeholder_album)
            .centerCrop()
            .transform(RoundedCorners(this.resources.getDimensionPixelSize(R.dimen.track_image_rounding_audiotplayer)))
            .into(binding.albumImage)

        audioPlayerInteractor.preparePlayer(url, object : AudioPlayerStatesListener {
            override fun onPrepared() {
            }

            override fun onCompletion() {
                handler.removeCallbacks(replayProgressRunnable)
                setImagePlaceholder(R.drawable.button_play)
                updateProgressTime()
            }
        })

        playStatusButton.setOnClickListener {
            startOrPause()
        }
    }

    override fun onPause() {
        super.onPause()
        audioPlayerInteractor.pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        audioPlayerInteractor.releasePlayer()
        handler.removeCallbacks(replayProgressRunnable)
    }


    private fun startOrPause() {
        when (audioPlayerInteractor.audioPlayerState) {
            PlayerState.STATE_PLAYING -> {
                audioPlayerInteractor.pausePlayer()
                setImagePlaceholder(R.drawable.button_play)
                handler.removeCallbacks(replayProgressRunnable)
            }

            PlayerState.STATE_PREPARED, PlayerState.STATE_PAUSED -> {
                audioPlayerInteractor.startPlayer()
                handler.post(replayProgressRunnable)
                setImagePlaceholder(R.drawable.pause_button)
            }

            PlayerState.STATE_DEFAULT -> Unit
        }
    }

    private fun setImagePlaceholder(image: Int) {
        playStatusButton.setImageResource(image)
    }

    private fun updateProgressTime() {
        binding.replayProgress.text = dateFormat.format(audioPlayerInteractor.getProgressTime())
    }
}