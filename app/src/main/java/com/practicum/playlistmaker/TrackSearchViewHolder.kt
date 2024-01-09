package com.practicum.playlistmaker

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.databinding.ActivitySearchBinding
import com.practicum.playlistmaker.databinding.TrackViewBinding
import java.text.SimpleDateFormat
import java.util.Locale

class TrackSearchViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bind(track: Track) {
        val binding = TrackViewBinding.bind(itemView)
        binding.trackName.text = track.trackName
        binding.bandName.text = track.artistName
        binding.trackDuration.text =
            SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTimeMillis)
        Glide.with(itemView)
            .load(track.artworkUrl100)
            .placeholder(R.drawable.placeholder)
            .centerCrop()
            .transform(RoundedCorners(itemView.resources.getDimensionPixelSize(R.dimen.track_image_rounding)))
            .into(binding.trackImage)
    }
}