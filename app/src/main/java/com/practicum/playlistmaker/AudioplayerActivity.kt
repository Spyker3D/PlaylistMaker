package com.practicum.playlistmaker

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.databinding.ActivityAudioplayerBinding
import java.text.SimpleDateFormat
import java.util.Locale

const val KEY_SELECTED_TRACK_DETAILS = "TRACK_DETAILS"

class AudioplayerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityAudioplayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.arrowBackAudioplayer.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

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
        Glide.with(this)
            .load(track?.getCoverArtwork())
            .placeholder(R.drawable.placeholder_album)
            .centerCrop()
            .transform(RoundedCorners(this.resources.getDimensionPixelSize(R.dimen.track_image_rounding_audiotplayer)))
            .into(binding.albumImage)
    }
}