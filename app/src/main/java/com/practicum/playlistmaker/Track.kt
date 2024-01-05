package com.practicum.playlistmaker

data class Track(
    val trackId: Int,
    val trackName: String,
    val artistName: String,
    val trackTimeMillis: Long, // SimpleDateFormat("mm:ss", Locale.getDefault()).format(293000L)
    val artworkUrl100: String
)