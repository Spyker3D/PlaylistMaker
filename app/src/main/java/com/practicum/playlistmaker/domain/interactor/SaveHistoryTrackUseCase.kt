package com.practicum.playlistmaker.domain.interactor

import com.practicum.playlistmaker.domain.entities.TrackInfo
import com.practicum.playlistmaker.domain.repository.TrackStorageRepository

class SaveHistoryTrackUseCase(private val trackStorage: TrackStorageRepository) {

    fun execute(historyTrackList: List<TrackInfo>) {
        trackStorage.saveHistoryTrackList(historyTrackList)
    }
}