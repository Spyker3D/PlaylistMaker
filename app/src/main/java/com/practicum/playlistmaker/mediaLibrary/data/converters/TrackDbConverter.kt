package com.practicum.playlistmaker.mediaLibrary.data.converters

import com.practicum.playlistmaker.mediaLibrary.data.db.entity.FavouriteTrackEntity
import com.practicum.playlistmaker.mediaLibrary.data.db.entity.TrackInPlaylistEntity
import com.practicum.playlistmaker.search.domain.entities.TrackInfo
import java.text.SimpleDateFormat
import java.util.Locale

object TrackDbConverter {

    private val trackTimeFormat by lazy { SimpleDateFormat("mm:ss", Locale.getDefault()) }

    private fun trackTimeMillisFormat(trackTimeMillis: Long): String {
        return trackTimeFormat.format(trackTimeMillis)
    }

    private fun makeLargePreview(previewUrl: String?): String? {
        return previewUrl?.replaceAfterLast('/', "512x512bb.jpg")
    }

    fun TrackInfo.mapToDbEntity(timeAdded: Long): FavouriteTrackEntity {
        return FavouriteTrackEntity(
            trackId = this.trackId,
            trackName = this.trackName,
            trackUrl = this.previewUrl,
            coverUrl = this.artworkUrl100,
            coverUrlLarge = this.artworkUrlLarge,
            artistName = this.artistName,
            collectionName = this.collectionName,
            releaseYear = this.releaseYear,
            genreName = this.primaryGenreName,
            country = this.country,
            trackTimeMillis = this.trackTimeMillis,
            timeAdded = timeAdded,
        )
    }

    fun FavouriteTrackEntity.mapToDomainEntity(): TrackInfo {
        return TrackInfo(
            trackId = this.trackId,
            trackName = this.trackName,
            artistName = this.artistName,
            trackTimeMillis = this.trackTimeMillis,
            trackTimeMillisFormatted = trackTimeMillisFormat(this.trackTimeMillis),
            artworkUrl100 = this.coverUrl,
            country = this.country,
            collectionName = this.collectionName,
            releaseDate = null,
            releaseYear = this.releaseYear,
            primaryGenreName = this.genreName,
            previewUrl = this.trackUrl,
            artworkUrlLarge = makeLargePreview(this.trackUrl)
        )
    }

    fun TrackInfo.mapToDbTrackInPlaylistsEntity(timeAdded: Long): TrackInPlaylistEntity {
        return TrackInPlaylistEntity(
            trackId = this.trackId,
            trackName = this.trackName,
            trackUrl = this.previewUrl,
            coverUrl = this.artworkUrl100,
            coverUrlLarge = this.artworkUrlLarge,
            artistName = this.artistName,
            collectionName = this.collectionName,
            releaseYear = this.releaseYear,
            genreName = this.primaryGenreName,
            country = this.country,
            trackTimeMillis = this.trackTimeMillis,
            timeAdded = timeAdded,
        )
    }

    fun TrackInPlaylistEntity.mapToDomainEntity(): TrackInfo {
        return TrackInfo(
            trackId = this.trackId,
            trackName = this.trackName,
            artistName = this.artistName,
            trackTimeMillis = this.trackTimeMillis,
            trackTimeMillisFormatted = trackTimeMillisFormat(this.trackTimeMillis),
            artworkUrl100 = this.coverUrl,
            country = this.country,
            collectionName = this.collectionName,
            releaseDate = null,
            releaseYear = this.releaseYear,
            primaryGenreName = this.genreName,
            previewUrl = this.trackUrl,
            artworkUrlLarge = makeLargePreview(this.trackUrl)
        )
    }

}





