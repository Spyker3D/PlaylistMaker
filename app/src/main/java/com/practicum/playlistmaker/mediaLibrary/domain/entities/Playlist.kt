package com.practicum.playlistmaker.mediaLibrary.domain.entities

data class Playlist(
    val playlistName: String,
    val playlistNameSecondary: String,
    val playlistDescription: String? = "",
    val pathToImage: String?,
    val numberOfTracks: Int = 0,
)