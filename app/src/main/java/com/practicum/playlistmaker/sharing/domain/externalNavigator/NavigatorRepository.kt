package com.practicum.playlistmaker.sharing.domain.externalNavigator

import com.practicum.playlistmaker.mediaLibrary.domain.entities.Playlist
import com.practicum.playlistmaker.search.presentation.entities.Track


interface NavigatorRepository {

    fun shareApp()

    fun openTerms()

    fun openEmail()

    fun sharePlaylist(playlist: Playlist, trackList: List<Track>)
}