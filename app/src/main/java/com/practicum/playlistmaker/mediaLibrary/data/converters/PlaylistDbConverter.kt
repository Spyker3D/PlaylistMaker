package com.practicum.playlistmaker.mediaLibrary.data.converters

import com.practicum.playlistmaker.mediaLibrary.data.db.entity.PlaylistEntity
import com.practicum.playlistmaker.mediaLibrary.domain.entities.Playlist

object PlaylistDbConverter {

    fun Playlist.mapToDbEntity(): PlaylistEntity {
        return PlaylistEntity(
            playlistName = this.playlistName,
            playlistDescription = this.playlistDescription,
            pathToImage = this.pathToImage,
            numberOfTracks = this.numberOfTracks,
        )
    }

    fun PlaylistEntity.mapToDomainEntity(): Playlist {
        return Playlist(
            playlistName = this.playlistName,
            playlistDescription = this.playlistDescription,
            pathToImage = this.pathToImage,
            numberOfTracks = this.numberOfTracks,
        )
    }
}