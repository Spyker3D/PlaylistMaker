package com.practicum.playlistmaker.sharing.interactor

import com.practicum.playlistmaker.mediaLibrary.domain.entities.Playlist
import com.practicum.playlistmaker.search.presentation.entities.Track
import com.practicum.playlistmaker.sharing.domain.externalNavigator.NavigatorRepository

class NavigatorInteractor(private val navigatorRepository: NavigatorRepository) {

    fun shareApp() {
        navigatorRepository.shareApp()
    }

    fun openTerms() {
        navigatorRepository.openTerms()
    }

    fun openEmail() {
        navigatorRepository.openEmail()
    }

    fun sharePlaylist(playlist: Playlist, trackList: List<Track>) {
        navigatorRepository.sharePlaylist(playlist = playlist, trackList = trackList)
    }
}