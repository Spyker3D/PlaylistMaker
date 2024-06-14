package com.practicum.playlistmaker.search.presentation.mapper

import com.practicum.playlistmaker.search.domain.entities.TrackInfo
import com.practicum.playlistmaker.search.presentation.entities.Track

object TrackPresentationMapper {

    fun Track.mapToDomain(): TrackInfo {
        return TrackInfo(
            trackId = this.trackId,
            trackName = this.trackName,
            artistName = this.artistName,
            trackTimeMillis = this.trackTimeMillis,
            trackTimeMillisFormatted = this.trackTimeMillisFormatted,
            artworkUrl100 = this.artworkUrl100,
            country = this.country,
            collectionName = this.collectionName,
            releaseDate = this.releaseDate,
            releaseYear = this.releaseYear,
            primaryGenreName = this.primaryGenreName,
            previewUrl = this.previewUrl,
            artworkUrlLarge = this.artworkUrlLarge,
            isFavourite = this.isFavorite,
        )
    }

    fun TrackInfo.mapToPresentation(): Track {
        return Track(
            trackId = this.trackId,
            trackName = this.trackName,
            artistName = this.artistName,
            trackTimeMillis = this.trackTimeMillis,
            trackTimeMillisFormatted = this.trackTimeMillisFormatted,
            artworkUrl100 = this.artworkUrl100,
            country = this.country,
            collectionName = this.collectionName,
            releaseDate = this.releaseDate,
            releaseYear = this.releaseYear,
            primaryGenreName = this.primaryGenreName,
            previewUrl = this.previewUrl,
            artworkUrlLarge = this.artworkUrlLarge,
            isFavorite = this.isFavourite,
        )
    }
    
}


