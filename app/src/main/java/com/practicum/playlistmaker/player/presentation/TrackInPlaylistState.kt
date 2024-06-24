package com.practicum.playlistmaker.player.presentation

sealed class TrackInPlaylistState(val trackStatus: Boolean, val playlistName: String) {
    class TrackIsInserted(trackStatus: Boolean = true, playlistName: String) :
        TrackInPlaylistState(trackStatus, playlistName)

    class TrackNotInserted(trackStatus: Boolean = false, playlistName: String) :
        TrackInPlaylistState(trackStatus, playlistName)
}

