package com.practicum.playlistmaker.search.ui.entities

import android.os.Parcelable
import com.practicum.playlistmaker.search.domain.entities.TrackInfo
import kotlinx.parcelize.Parcelize

@Parcelize
data class Track (
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
    val artworkUrlLarge: String?,
) : Parcelable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is TrackInfo) return false
        if (this.trackId != (other as TrackInfo).trackId) return false
        return true
    }

    override fun hashCode(): Int {
        return trackId.hashCode()
    }
}