package com.practicum.playlistmaker.domain.entities

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class TrackInfo(
    val trackId: Int,
    val trackName: String?,
    val artistName: String?,
    val trackTimeMillis: Long,
    val trackTimeMillisFormatted: String,
    val artworkUrl100: String?,
    val country: String?,
    val collectionName: String?,
    val releaseDate: String?,
    val releaseYear: String?,
    val primaryGenreName: String?,
    val previewUrl: String?,
    val artworkUrlLarge: String?
    ) : Parcelable {
}

