package com.practicum.playlistmaker.mediaLibrary.data.repository

import com.practicum.playlistmaker.mediaLibrary.data.converters.TrackDbConverter
import com.practicum.playlistmaker.mediaLibrary.data.converters.TrackDbConverter.mapToDbEntity
import com.practicum.playlistmaker.mediaLibrary.data.converters.TrackDbConverter.mapToDomainEntity
import com.practicum.playlistmaker.mediaLibrary.data.db.AppDatabase
import com.practicum.playlistmaker.mediaLibrary.domain.repository.FavouriteTracksRepository
import com.practicum.playlistmaker.search.domain.entities.TrackInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FavouriteTracksRepositoryImpl(private val appDatabase: AppDatabase) :
    FavouriteTracksRepository {
    override suspend fun insertToFavouriteTracks(track: TrackInfo) {
        appDatabase.trackDao().insertToFavouriteTracks(track.mapToDbEntity())
    }

    override suspend fun deleteFromFavouriteTracks(track: TrackInfo) {
        appDatabase.trackDao().deleteFromFavouriteTracks(track.mapToDbEntity())
    }

    override fun getFavouriteTracks(): Flow<List<TrackInfo>> = flow {
        val favouriteTracksList = appDatabase.trackDao().getFavouriteTracks()
        emit(favouriteTracksList.map { it.mapToDomainEntity() })
    }

    override suspend fun updateTrackStatus(track: TrackInfo) {
        val favouriteTracksIds = appDatabase.trackDao().getFavouriteTracksIds()
        track.isFavourite = track.trackId in favouriteTracksIds
    }

}


