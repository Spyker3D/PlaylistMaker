package com.practicum.playlistmaker.mediaLibrary.domain.interactor

import com.practicum.playlistmaker.mediaLibrary.domain.repository.FavouriteTracksRepository
import com.practicum.playlistmaker.search.data.dto.TrackDto
import com.practicum.playlistmaker.search.domain.entities.TrackInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FavouriteTracksInteractor(private val favouriteTracksRepository: FavouriteTracksRepository) {
    suspend fun insertToFavouriteTracks(track: TrackInfo) {
        favouriteTracksRepository.insertToFavouriteTracks(track)
    }

    suspend fun deleteFromFavouriteTracks(track: TrackInfo) {
        favouriteTracksRepository.deleteFromFavouriteTracks(track)
    }

    fun getFavouriteTracks(): Flow<List<TrackInfo>> = favouriteTracksRepository.getFavouriteTracks()

    suspend fun updateTrackStatus(track: TrackInfo) {
        favouriteTracksRepository.updateTrackStatus(track)
    }

}