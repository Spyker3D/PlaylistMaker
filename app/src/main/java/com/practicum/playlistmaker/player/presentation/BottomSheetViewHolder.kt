package com.practicum.playlistmaker.player.presentation

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.AudioplayerPlaylistItemViewBinding
import com.practicum.playlistmaker.mediaLibrary.data.repository.getFileByPlaylistName
import com.practicum.playlistmaker.mediaLibrary.domain.entities.Playlist

class BottomSheetViewHolder(private val binding: AudioplayerPlaylistItemViewBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(playlist: Playlist) {
        binding.audioplayerPlaylistName.text = playlist.playlistNameSecondary
        binding.audioplayerNumberOfTracks.text = getNumberOfTracks(playlist.numberOfTracks)

        val imageFile = binding.root.context.getFileByPlaylistName(playlist.playlistName)

        Glide.with(itemView)
            .load(imageFile)
            .placeholder(R.drawable.placeholder_album)
            .centerCrop()
            .transform(RoundedCorners(itemView.resources.getDimensionPixelSize(R.dimen.track_image_rounding_audiotplayer)))
            .into(binding.audioplayerPlaylistImage)
    }

    private fun getNumberOfTracks(count: Int): String {
        val pluralsString = binding.root.resources.getQuantityString(
            R.plurals.number_of_tracks_plurals,
            count,
            count
        )
        return pluralsString
    }
}