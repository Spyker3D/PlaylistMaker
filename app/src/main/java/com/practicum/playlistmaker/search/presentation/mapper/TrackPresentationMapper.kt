package com.practicum.playlistmaker.search.presentation.mapper

import com.practicum.playlistmaker.search.domain.entities.TrackInfo
import com.practicum.playlistmaker.search.presentation.entities.Track

object TrackPresentationMapper {

    fun mapToPresentation(trackInfoList: List<TrackInfo>): List<Track> {
        return trackInfoList.map {
            Track(
                trackId = it.trackId,
                trackName = it.trackName,
                artistName = it.artistName,
                trackTimeMillis = it.trackTimeMillis,
                trackTimeMillisFormatted = it.trackTimeMillisFormatted,
                artworkUrl100 = it.artworkUrl100,
                country = it.country,
                collectionName = it.collectionName,
                releaseDate = it.releaseDate,
                releaseYear = it.releaseYear,
                primaryGenreName = it.primaryGenreName,
                previewUrl = it.previewUrl,
                artworkUrlLarge = it.artworkUrlLarge,
            )
        }
    }

    fun mapToDomain(trackList: List<Track>): List<TrackInfo> {
        return trackList.map {
            TrackInfo(
                trackId = it.trackId,
                trackName = it.trackName,
                artistName = it.artistName,
                trackTimeMillis = it.trackTimeMillis,
                trackTimeMillisFormatted = it.trackTimeMillisFormatted,
                artworkUrl100 = it.artworkUrl100,
                country = it.country,
                collectionName = it.collectionName,
                releaseDate = it.releaseDate,
                releaseYear = it.releaseYear,
                primaryGenreName = it.primaryGenreName,
                previewUrl = it.previewUrl,
                artworkUrlLarge = it.artworkUrlLarge,
            )
        }
    }
}


