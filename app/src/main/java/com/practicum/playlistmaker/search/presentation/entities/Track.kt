package com.practicum.playlistmaker.search.presentation.entities

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class Track(
    val trackId: Int,
    val trackName: String,
    val artistName: String,
    val trackTimeMillis: Long,
    val trackTimeMillisFormatted: String,
    val artworkUrl100: String?,
    val country: String?,
    val collectionName: String?,
    val releaseDate: String?,
    val releaseYear: String?,
    val primaryGenreName: String?,
    val previewUrl: String,
    val artworkUrlLarge: String?,
    var isFavorite: Boolean = false,
) : Parcelable {
    override fun equals(other: Any?): Boolean =
        this === other || other is Track && trackId == other.trackId

    override fun hashCode(): Int {
        return trackId
    }
}