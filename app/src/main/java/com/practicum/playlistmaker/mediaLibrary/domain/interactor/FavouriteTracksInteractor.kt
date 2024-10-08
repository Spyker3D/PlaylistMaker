package com.practicum.playlistmaker.mediaLibrary.domain.interactor

import com.practicum.playlistmaker.mediaLibrary.domain.repository.FavouriteTracksRepository
import com.practicum.playlistmaker.search.domain.entities.TrackInfo
import kotlinx.coroutines.flow.Flow

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

    suspend fun isFavourite(track: TrackInfo): Boolean {
        return favouriteTracksRepository.isFavourite(track)
    }

}