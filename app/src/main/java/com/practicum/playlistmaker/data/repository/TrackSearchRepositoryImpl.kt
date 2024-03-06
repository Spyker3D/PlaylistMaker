package com.practicum.playlistmaker.data.repository

import com.practicum.playlistmaker.data.dto.TrackSearchRequest
import com.practicum.playlistmaker.data.dto.TracksSearchResponse
import com.practicum.playlistmaker.data.mapper.TrackMapper
import com.practicum.playlistmaker.data.network.NetworkClient
import com.practicum.playlistmaker.domain.entities.Resource
import com.practicum.playlistmaker.domain.entities.TrackInfo
import com.practicum.playlistmaker.domain.repository.TrackSearchRepository

class TrackSearchRepositoryImpl(private val networkClient: NetworkClient) : TrackSearchRepository {
    override fun searchTrack(request: String): Resource<List<TrackInfo>> {
        val response = networkClient.doRequest(TrackSearchRequest(request))

        return if (response is TracksSearchResponse) {
            val trackList: List<TrackInfo> = TrackMapper.mapToDomain(response.results)
            Resource.Success(trackList)
        } else if (response.resultCode == 0) {
            Resource.InternetConnectionError()
        } else {
            Resource.Error()
        }
    }
}

