package com.practicum.playlistmaker.search.data.repository

import com.practicum.playlistmaker.mediaLibrary.data.db.AppDatabase
import com.practicum.playlistmaker.search.data.dto.TrackSearchRequest
import com.practicum.playlistmaker.search.data.dto.TracksSearchResponse
import com.practicum.playlistmaker.search.data.mapper.TrackMapper
import com.practicum.playlistmaker.search.data.mapper.TrackMapper.mapToDomain
import com.practicum.playlistmaker.search.data.network.NetworkClient
import com.practicum.playlistmaker.search.domain.entities.Resource
import com.practicum.playlistmaker.search.domain.entities.TrackInfo
import com.practicum.playlistmaker.search.domain.repository.TrackSearchRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class TrackSearchRepositoryImpl(
    private val networkClient: NetworkClient,
    private val appDatabase: AppDatabase,
) : TrackSearchRepository {
    override fun searchTrack(request: String): Flow<Resource<List<TrackInfo>?>> = flow {
        val response = networkClient.doRequest(TrackSearchRequest(request))
        when (response.resultCode) {
            -1 -> {
                emit(Resource.InternetConnectionError())
            }

            200 -> {
                val trackList: List<TrackInfo> =
                    (response as TracksSearchResponse).results.map { it.mapToDomain() }
                if ((response as TracksSearchResponse).results.isNotEmpty()) {
                    val favouriteTracksIds = appDatabase.trackDao().getFavouriteTracksIds()

                    trackList.forEach {
                        if (it.trackId in favouriteTracksIds) {
                            it.isFavourite = true
                        }
                    }
                    emit(Resource.Success(data = trackList))
                } else emit(Resource.Success(listOf<TrackInfo>()))
            }

            else -> emit(Resource.Error())
        }
    }

}

