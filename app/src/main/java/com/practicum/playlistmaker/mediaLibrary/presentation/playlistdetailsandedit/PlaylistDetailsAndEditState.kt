package com.practicum.playlistmaker.mediaLibrary.presentation.playlistdetailsandedit

import com.practicum.playlistmaker.mediaLibrary.domain.entities.Playlist
import com.practicum.playlistmaker.search.presentation.entities.Track

sealed interface PlaylistDetailsAndEditState {

    data class Content(
        val playlist: Playlist,
        val tracksList: List<Track>,
        val playlistLength: Int,
    ) :
        PlaylistDetailsAndEditState

    data object Empty : PlaylistDetailsAndEditState
}