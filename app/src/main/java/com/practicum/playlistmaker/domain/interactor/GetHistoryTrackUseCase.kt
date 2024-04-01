package com.practicum.playlistmaker.domain.interactor

import com.practicum.playlistmaker.domain.entities.TrackInfo
import com.practicum.playlistmaker.domain.repository.TrackStorageRepository

class GetHistoryTrackUseCase(private val trackStorage: TrackStorageRepository) {

    fun execute(): List<TrackInfo> {
        return trackStorage.getHistoryTrackList()
    }
}