package com.practicum.playlistmaker.search.domain.interactor

import com.practicum.playlistmaker.search.domain.entities.TrackInfo
import com.practicum.playlistmaker.search.domain.repository.TrackStorageRepository

class SelectedTrackInteractor(private val trackStorage: TrackStorageRepository) {

    fun getSelectedTrack(): TrackInfo {
        return trackStorage.getSelectedTrack()
    }

    fun saveSelectedTrack(selectedTrack: TrackInfo) {
        trackStorage.saveSelectedTrack(selectedTrack)
    }
}