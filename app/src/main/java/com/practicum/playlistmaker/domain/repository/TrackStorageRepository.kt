package com.practicum.playlistmaker.domain.repository

import com.practicum.playlistmaker.domain.entities.TrackInfo

interface TrackStorageRepository {

    fun saveHistoryTrackList(historyTrackListToSave: List<TrackInfo>)

    fun getHistoryTrackList(): List<TrackInfo>
}