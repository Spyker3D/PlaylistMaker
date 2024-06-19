package com.practicum.playlistmaker.search.data.mapper

import com.practicum.playlistmaker.search.data.dto.TrackDto
import com.practicum.playlistmaker.search.domain.entities.TrackInfo
import java.text.SimpleDateFormat
import java.util.Locale

object TrackMapper {

    private val trackTimeFormat by lazy { SimpleDateFormat("mm:ss", Locale.getDefault()) }
    private val releaseDateFormatDetailed by lazy {
        SimpleDateFormat(
            "yyyy-MM-dd'T'HH:mm:ss'Z'",
            Locale.getDefault()
        )
    }
    private val releaseYearFormat by lazy { SimpleDateFormat("yyyy", Locale.getDefault()) }

    private fun trackTimeMillisFormat(trackTimeMillis: Long): String {
        return trackTimeFormat.format(trackTimeMillis)
    }

    private fun formatReleaseDateToYear(releaseDate: String?): String? {
        return if (releaseDate != null) {
            releaseDateFormatDetailed.parse(releaseDate)?.let { releaseYearFormat.format(it) }
        } else null
    }

    private fun makeLargePreview(previewUrl: String?): String? {
        return previewUrl?.replaceAfterLast('/', "512x512bb.jpg")
    }

    private fun formatYearToReleaseDate(releaseYear: String?): String? {
        return if (releaseYear != null) {
            releaseYearFormat.parse(releaseYear)?.let { releaseDateFormatDetailed.format(it) }
        } else null
    }

    fun TrackDto.mapToDomain(): TrackInfo {
        return TrackInfo(
            trackId = this.trackId,
            trackName = this.trackName,
            artistName = this.artistName,
            trackTimeMillis = this.trackTimeMillis,
            trackTimeMillisFormatted = trackTimeMillisFormat(this.trackTimeMillis),
            artworkUrl100 = this.artworkUrl100,
            country = this.country,
            collectionName = this.collectionName,
            releaseDate = this.releaseDate,
            releaseYear = formatReleaseDateToYear(this.releaseDate),
            primaryGenreName = this.primaryGenreName,
            previewUrl = this.previewUrl,
            artworkUrlLarge = makeLargePreview(this.artworkUrl100)
        )
    }

    fun TrackInfo.mapToStorage(): TrackDto {
        return TrackDto(
            trackId = this.trackId,
            trackName = this.trackName,
            artistName = this.artistName,
            trackTimeMillis = this.trackTimeMillis,
            artworkUrl100 = this.artworkUrl100,
            country = this.country,
            collectionName = this.collectionName,
            releaseDate = formatYearToReleaseDate(this.releaseYear),
            primaryGenreName = this.primaryGenreName,
            previewUrl = this.previewUrl
        )
    }

}



