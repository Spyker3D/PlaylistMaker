package com.practicum.playlistmaker.data.mapper

import com.practicum.playlistmaker.data.dto.TrackDto
import com.practicum.playlistmaker.domain.entities.TrackInfo
import java.text.SimpleDateFormat
import java.util.Locale

object TrackMapper {

    fun mapToDomain(trackList: List<TrackDto>): List<TrackInfo> {
        return trackList.map {
            TrackInfo(
                trackId = it.trackId,
                trackName = it.trackName,
                artistName = it.artistName,
                trackTimeMillis = it.trackTimeMillis,
                trackTimeMillisFormatted = trackTimeMillisFormat(it.trackTimeMillis),
                artworkUrl100 = it.artworkUrl100,
                country = it.country,
                collectionName = it.collectionName,
                releaseDate = it.releaseDate,
                releaseYear = releaseYearFormat(it.releaseDate),
                primaryGenreName = it.primaryGenreName,
                previewUrl = it.previewUrl,
                artworkUrlLarge = makeLargePreview(it.artworkUrl100)
            )
        }
    }

    private fun trackTimeMillisFormat(trackTimeMillis: Long): String {
        return SimpleDateFormat("mm:ss", Locale.getDefault()).format(trackTimeMillis)
    }

    private fun releaseYearFormat(releaseDate: String?): String? {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
        return if (releaseDate != null) {
            inputFormat.parse(releaseDate)
                ?.let {
                    SimpleDateFormat("yyyy", Locale.getDefault()).format(it)
                }
        } else null
    }

    private fun makeLargePreview(previewUrl: String?): String? {
        return previewUrl?.replaceAfterLast('/', "512x512bb.jpg")
    }

    fun mapToStorage(trackList: List<TrackInfo>): List<TrackDto> {
        return trackList.map {
            TrackDto(
                trackId = it.trackId,
                trackName = it.trackName,
                artistName = it.artistName,
                trackTimeMillis = it.trackTimeMillis,
                artworkUrl100 = it.artworkUrl100,
                country = it.country,
                collectionName = it.collectionName,
                releaseDate = formatYearToReleaseDate(it.releaseYear),
                primaryGenreName = it.primaryGenreName,
                previewUrl = it.previewUrl
            )
        }
    }

    private fun formatYearToReleaseDate(releaseYear: String?): String? {
        val inputFormat = SimpleDateFormat("yyyy", Locale.getDefault())
        return if (releaseYear != null) {
            inputFormat.parse(releaseYear)
                ?.let {
                    SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault()).format(it)
                }
        } else null
    }
}



