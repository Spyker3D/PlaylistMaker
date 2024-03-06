package com.practicum.playlistmaker.data.repository

import com.practicum.playlistmaker.data.dto.TrackDto
import com.practicum.playlistmaker.data.mapper.TrackMapper
import com.practicum.playlistmaker.data.storage.TrackStorage
import com.practicum.playlistmaker.domain.entities.TrackInfo
import com.practicum.playlistmaker.domain.repository.TrackStorageRepository

class TrackStorageRepositoryImpl(private val trackStorage: TrackStorage) : TrackStorageRepository {
    override fun saveHistoryTrackList(historyTrackListToSave: List<TrackInfo>) {
        val historyTrackListToSave = TrackMapper.mapToStorage(historyTrackListToSave)
        trackStorage.saveHistoryTrackList(historyTrackListToSave)
    }

    override fun getHistoryTrackList(): List<TrackInfo> {
        val historyTrackListToGet = trackStorage.getHistoryTrackList()
        return TrackMapper.mapToDomain(historyTrackListToGet)
    }
}