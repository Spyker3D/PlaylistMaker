package com.practicum.playlistmaker.search.data.repository

import android.util.Log
import com.practicum.playlistmaker.search.data.dto.TrackSearchRequest
import com.practicum.playlistmaker.search.data.dto.TracksSearchResponse
import com.practicum.playlistmaker.search.data.mapper.TrackMapper
import com.practicum.playlistmaker.search.data.network.NetworkClient
import com.practicum.playlistmaker.search.domain.entities.Resource
import com.practicum.playlistmaker.search.domain.entities.TrackInfo
import com.practicum.playlistmaker.search.domain.repository.TrackSearchRepository

class TrackSearchRepositoryImpl(private val networkClient: NetworkClient) : TrackSearchRepository {
    override fun searchTrack(request: String): Resource<List<TrackInfo>> {
        val response = networkClient.doRequest(TrackSearchRequest(request))

        return when (response.resultCode) {
            -1 -> Resource.InternetConnectionError()

            200 -> {
                val trackList: List<TrackInfo> =
                    TrackMapper.mapToDomain((response as TracksSearchResponse).results)
                if ((response as TracksSearchResponse).results.isNotEmpty()) {
                    Resource.Success(trackList)
                } else Resource.Success(listOf<TrackInfo>())
            }

            else -> Resource.Error()
        }
    }
}

