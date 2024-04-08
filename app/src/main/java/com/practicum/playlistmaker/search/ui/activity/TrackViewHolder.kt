package com.practicum.playlistmaker.search.ui.activity

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.TrackViewBinding
import com.practicum.playlistmaker.search.domain.entities.TrackInfo

class TrackViewHolder(private val binding: TrackViewBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bind(track: TrackInfo) {
        binding.trackName.text = track.trackName
        binding.bandName.text = track.artistName
        binding.trackDuration.text = track.trackTimeMillisFormatted
        Glide.with(itemView)
            .load(track.artworkUrl100)
            .placeholder(R.drawable.placeholder_track)
            .centerCrop()
            .transform(RoundedCorners(itemView.resources.getDimensionPixelSize(R.dimen.track_image_rounding)))
            .into(binding.trackImage)
    }
}