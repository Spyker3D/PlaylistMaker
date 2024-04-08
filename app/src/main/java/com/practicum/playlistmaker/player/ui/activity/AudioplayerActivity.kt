package com.practicum.playlistmaker.player.ui.activity

import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ActivityAudioplayerBinding
import com.practicum.playlistmaker.player.domain.entities.AudioPlayerStatesListener
import com.practicum.playlistmaker.player.ui.viewModel.PlayerViewModel
import com.practicum.playlistmaker.search.domain.entities.TrackInfo
import java.text.SimpleDateFormat
import java.util.Locale

const val KEY_SELECTED_TRACK_DETAILS = "TRACK_DETAILS"

class AudioplayerActivity : AppCompatActivity() {

    private lateinit var playStatusButton: ImageButton
    private lateinit var url: String
    private lateinit var binding: ActivityAudioplayerBinding

    private val dateFormat by lazy { SimpleDateFormat("m:ss", Locale.getDefault()) }

    private lateinit var viewModel: PlayerViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAudioplayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.arrowBackAudioplayer.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        playStatusButton = binding.playStatusPlaceholder

        viewModel = ViewModelProvider(
            this,
            PlayerViewModel.getViewModelFactory()
        )[PlayerViewModel::class.java]

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

        viewModel.progressTimeLiveData.observe(this) {
            binding.replayProgress.text = dateFormat.format(it)
        }

        viewModel.isPlaying.observe(this) {
            when (it) {
                true -> setImagePlaceholder(R.drawable.pause_button)
                false -> setImagePlaceholder(R.drawable.button_play)
            }
        }

        viewModel.preparePlayer(
            object : AudioPlayerStatesListener {
                override fun onPrepared() {
                }

                override fun onCompletion() {
                    setImagePlaceholder(R.drawable.button_play)
                    binding.replayProgress.text = dateFormat.format(viewModel.getProgressTime())
                }
            })

        playStatusButton.setOnClickListener { viewModel.startOrPause() }
    }

    override fun onPause() {
        super.onPause()
        viewModel.pausePlayer()
    }

    private fun setImagePlaceholder(image: Int) {
        playStatusButton.setImageResource(image)
    }
}