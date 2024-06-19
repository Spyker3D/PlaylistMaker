package com.practicum.playlistmaker.search.domain.interactor

import com.practicum.playlistmaker.search.domain.entities.TrackInfo
import com.practicum.playlistmaker.search.domain.repository.TrackStorageRepository

class GetHistoryTrackUseCase(private val trackStorage: TrackStorageRepository) {

    suspend fun execute(): List<TrackInfo> {
        return trackStorage.getHistoryTrackList()
    }
}