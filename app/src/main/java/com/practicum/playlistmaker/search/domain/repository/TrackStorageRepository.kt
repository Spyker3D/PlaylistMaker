package com.practicum.playlistmaker.search.domain.repository

import com.practicum.playlistmaker.search.domain.entities.TrackInfo

interface TrackStorageRepository {

    fun saveHistoryTrackList(historyTrackListToSave: List<TrackInfo>)

    fun getHistoryTrackList(): List<TrackInfo>

}