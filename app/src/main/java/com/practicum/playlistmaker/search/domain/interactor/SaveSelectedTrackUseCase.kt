package com.practicum.playlistmaker.search.domain.interactor

import com.practicum.playlistmaker.search.domain.entities.TrackInfo
import com.practicum.playlistmaker.search.domain.repository.TrackStorageRepository

class SaveSelectedTrackUseCase(private val trackStorage: TrackStorageRepository) {

    fun saveSelectedTrack(selectedTrack: TrackInfo) {
        trackStorage.saveSelectedTrack(selectedTrack)
    }
}