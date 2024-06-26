package com.practicum.playlistmaker.mediaLibrary.domain.entities

import androidx.room.ColumnInfo
import androidx.room.PrimaryKey

data class Playlist(
    val playlistName: String,
    val playlistDescription: String? = "",
    val pathToImage: String?,
    val numberOfTracks: Int = 0,
)