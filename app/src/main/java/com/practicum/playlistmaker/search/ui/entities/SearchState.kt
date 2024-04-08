package com.practicum.playlistmaker.search.ui.entities

import com.practicum.playlistmaker.search.domain.entities.TrackInfo

sealed interface SearchState {

    object Loading : SearchState

    data class HistoryListPresentation(val historyTrackList: List<TrackInfo>): SearchState
    data class Content(val trackList: List<TrackInfo>) : SearchState

    data class Error(val errorMessage: String) : SearchState // Реализовать передачу текста ошибки как было в примере.

    data class Empty(val message: String) : SearchState
}