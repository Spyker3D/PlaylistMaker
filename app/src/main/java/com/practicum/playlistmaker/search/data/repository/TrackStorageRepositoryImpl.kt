package com.practicum.playlistmaker.search.data.repository

import com.practicum.playlistmaker.search.data.mapper.TrackMapper
import com.practicum.playlistmaker.search.data.storage.TrackStorage
import com.practicum.playlistmaker.search.domain.entities.TrackInfo
import com.practicum.playlistmaker.search.domain.repository.TrackStorageRepository

class TrackStorageRepositoryImpl(private val trackStorage: TrackStorage) : TrackStorageRepository {
    override fun saveHistoryTrackList(historyTrackListToSave: List<TrackInfo>) {
        val historyTrackListToSaveMapped = TrackMapper.mapToStorage(historyTrackListToSave)
        trackStorage.saveHistoryTrackList(historyTrackListToSaveMapped)
    }

    override fun getHistoryTrackList(): List<TrackInfo> {
        val historyTrackListToGet = trackStorage.getHistoryTrackList()
        return TrackMapper.mapToDomain(historyTrackListToGet)
    }

    override fun saveSelectedTrack(selectedTrack: TrackInfo) {
        val selectedTrackDto = TrackMapper.mapToStorage(listOf(selectedTrack))
        trackStorage.saveSelectedTrack(selectedTrackDto.first())
    }
}