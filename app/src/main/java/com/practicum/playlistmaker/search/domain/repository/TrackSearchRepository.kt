package com.practicum.playlistmaker.search.domain.repository

import com.practicum.playlistmaker.search.domain.entities.Resource
import com.practicum.playlistmaker.search.domain.entities.TrackInfo
import kotlinx.coroutines.flow.Flow

interface TrackSearchRepository {

    fun searchTrack(request: String): Flow<Resource<List<TrackInfo>?>>
}