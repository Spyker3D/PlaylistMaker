package com.practicum.playlistmaker

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.databinding.ActivityAudioplayerBinding
import java.text.SimpleDateFormat
import java.util.Locale

const val KEY_SELECTED_TRACK_DETAILS = "TRACK_DETAILS"
const val UPDATE_PLAY_PROGRESS_DEBOUNCE_DELAY = 300L

class AudioplayerActivity : AppCompatActivity() {

    private lateinit var playStatusButton: ImageButton
    private var mediaPlayer = MediaPlayer()
    private var playerState = PlayerState.STATE_DEFAULT
    private lateinit var url: String
    private val handler = Handler(Looper.getMainLooper())
    private val replayProgressRunnable = object : Runnable {
        override fun run() {
            updateProgressTime()
            if (playerState == PlayerState.STATE_PLAYING) {
                handler.postDelayed(this, UPDATE_PLAY_PROGRESS_DEBOUNCE_DELAY)
            }
        }
    }
    private lateinit var binding: ActivityAudioplayerBinding
    private val dateFormat by lazy { SimpleDateFormat("mm:ss", Locale.getDefault()) }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAudioplayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.arrowBackAudioplayer.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        playStatusButton = binding.playStatusPlaceholder

        val track = intent.extras?.getParcelable(KEY_SELECTED_TRACK_DETAILS) as Track?

        binding.trackName.text = track?.trackName
        binding.bandName.text = track?.artistName
        binding.albumNamePlaceholder.text = track?.collectionName
        binding.lengthPlaceholder.text =
            track?.formatTrackLength()

        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
        binding.yearPlaceholder.text = inputFormat.parse(track?.releaseDate.toString())?.let {
            SimpleDateFormat(
                "yyyy",
                Locale.getDefault()
            ).format(it)
        }
        binding.genreNamePlaceholder.text = track?.primaryGenreName
        binding.countryNamePlaceholder.text = track?.country
        url = track?.previewUrl.toString()
        Glide.with(this)
            .load(track?.getCoverArtwork())
            .placeholder(R.drawable.placeholder_album)
            .centerCrop()
            .transform(RoundedCorners(this.resources.getDimensionPixelSize(R.dimen.track_image_rounding_audiotplayer)))
            .into(binding.albumImage)

        preparePlayer()
        playStatusButton.setOnClickListener {
            playbackControl()
        }
    }

    override fun onPause() {
        super.onPause()
        pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
        handler.removeCallbacks(replayProgressRunnable)
    }

    private fun preparePlayer() {
        mediaPlayer.setDataSource(url)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            playerState = PlayerState.STATE_PREPARED
        }
        mediaPlayer.setOnCompletionListener {
            handler.removeCallbacks(replayProgressRunnable)
            setImagePlaceholder(R.drawable.button_play)
            playerState = PlayerState.STATE_PREPARED
            binding.replayProgress.text = dateFormat.format(0)
        }
    }

    private fun startPlayer() {
        mediaPlayer.start()
        binding.replayProgress.text = dateFormat.format(0)
        setImagePlaceholder(R.drawable.pause_button)
        playerState = PlayerState.STATE_PLAYING
        handler.post(replayProgressRunnable)
    }

    private fun pausePlayer() {
        mediaPlayer.pause()
        setImagePlaceholder(R.drawable.button_play)
        playerState = PlayerState.STATE_PAUSED
        handler.removeCallbacks(replayProgressRunnable)
    }

    private fun playbackControl() {
        when (playerState) {
            PlayerState.STATE_PLAYING -> {
                pausePlayer()
            }

            PlayerState.STATE_PREPARED, PlayerState.STATE_PAUSED -> {
                startPlayer()
            }

            PlayerState.STATE_DEFAULT -> Unit
        }
    }

    private fun setImagePlaceholder(image: Int) {
        playStatusButton.setImageResource(image)
    }

    private fun updateProgressTime() {
        binding.replayProgress.text =
            dateFormat.format(mediaPlayer.currentPosition)
    }
}