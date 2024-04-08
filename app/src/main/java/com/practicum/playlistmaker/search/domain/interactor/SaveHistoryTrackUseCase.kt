package com.practicum.playlistmaker.search.domain.interactor

import com.practicum.playlistmaker.search.domain.entities.TrackInfo
import com.practicum.playlistmaker.search.domain.repository.TrackStorageRepository

class SaveHistoryTrackUseCase(private val trackStorage: TrackStorageRepository) {

    fun execute(historyTrackList: List<TrackInfo>) {
        trackStorage.saveHistoryTrackList(historyTrackList)
    }
}