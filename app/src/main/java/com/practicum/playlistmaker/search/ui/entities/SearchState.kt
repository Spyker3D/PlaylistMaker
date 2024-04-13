package com.practicum.playlistmaker.search.ui.entities

import com.practicum.playlistmaker.search.domain.entities.TrackInfo

sealed interface SearchState {

    object Loading : SearchState

    data class HistoryListPresentation(val historyTrackList: List<Track>): SearchState
    data class Content(val trackList: List<Track>) : SearchState

    data class Error(val errorMessage: String) : SearchState

    data class InternetConnectionError(val errorMessage: String) : SearchState

    data class Empty(val message: String) : SearchState
}