package com.practicum.playlistmaker.search.domain.repository

import com.practicum.playlistmaker.search.domain.entities.Resource
import com.practicum.playlistmaker.search.domain.entities.TrackInfo

interface TrackSearchRepository {

    fun searchTrack(request: String): Resource<List<TrackInfo>>
}