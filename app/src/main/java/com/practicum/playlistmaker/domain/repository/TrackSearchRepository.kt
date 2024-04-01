package com.practicum.playlistmaker.domain.repository

import com.practicum.playlistmaker.domain.entities.Resource
import com.practicum.playlistmaker.domain.entities.TrackInfo

interface TrackSearchRepository {

    fun searchTrack(request: String): Resource<List<TrackInfo>>
}