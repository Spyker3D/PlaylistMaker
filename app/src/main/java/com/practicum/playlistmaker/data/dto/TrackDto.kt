package com.practicum.playlistmaker.data.dto

data class TrackDto(
    val trackId: Int,
    val trackName: String?,
    val artistName: String?,
    val trackTimeMillis: Long,
    val artworkUrl100: String?,
    val country: String?,
    val collectionName: String?,
    val releaseDate: String?,
    val primaryGenreName: String?,
    val previewUrl: String?
)