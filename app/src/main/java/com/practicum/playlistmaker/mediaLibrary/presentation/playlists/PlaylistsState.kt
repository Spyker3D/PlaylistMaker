package com.practicum.playlistmaker.mediaLibrary.presentation.playlists

import com.practicum.playlistmaker.mediaLibrary.domain.entities.Playlist

sealed interface PlaylistsState {
    data class Content(val playlistsList: List<Playlist>) : PlaylistsState

    data object Empty : PlaylistsState
}
