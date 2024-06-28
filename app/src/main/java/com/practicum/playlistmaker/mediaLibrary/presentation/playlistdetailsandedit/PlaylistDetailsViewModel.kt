package com.practicum.playlistmaker.mediaLibrary.presentation.playlistdetailsandedit

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.mediaLibrary.domain.entities.Playlist
import com.practicum.playlistmaker.mediaLibrary.domain.interactor.PlaylistInteractor
import com.practicum.playlistmaker.search.presentation.entities.Track
import com.practicum.playlistmaker.search.presentation.mapper.TrackPresentationMapper.mapToPresentation
import com.practicum.playlistmaker.sharing.interactor.NavigatorInteractor
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class PlaylistDetailsViewModel(
    private val playlistName: String,
    private val playlistInteractor: PlaylistInteractor,
    private val navigatorInteractor: NavigatorInteractor,
) : ViewModel() {

    private val minutesFormat = SimpleDateFormat("mm", Locale.getDefault())

    private val _playlistState =
        MutableLiveData<PlaylistDetailsState>(PlaylistDetailsState.Empty)
    val playlistsState: LiveData<PlaylistDetailsState> = _playlistState

    fun loadPlaylistDetails() {
        viewModelScope.launch {
            playlistInteractor.getAllPlaylistDetails(playlistName).collect {
                val playlist: Playlist = it.first
                val trackListDomainSorted =
                    it.second.sortedByDescending { trackInfo -> trackInfo.timeAdded }
                val tracksList: List<Track> =
                    trackListDomainSorted.map { trackInfo -> trackInfo.mapToPresentation() }
                val playlistLength = getPlaylistLength(tracksList)

                _playlistState.value =
                    PlaylistDetailsState.Content(
                        playlist = playlist,
                        tracksList = tracksList,
                        playlistLength = playlistLength
                    )
            }
        }
    }

    fun deleteTrackFromPlaylist(trackId: Int, playlistName: String) {
        viewModelScope.launch {
            playlistInteractor.deleteTrackFromPlaylist(trackId, playlistName)
            loadPlaylistDetails()
        }
    }

    fun sharePlaylist() {
        viewModelScope.launch {
            val value: PlaylistDetailsState? = _playlistState.value
            if (value is PlaylistDetailsState.Content) {
                navigatorInteractor.sharePlaylist(value.playlist, value.tracksList)
            }
        }
    }

    fun deletePlaylist() {
        viewModelScope.launch {
            val value = _playlistState.value
            if (value is PlaylistDetailsState.Content) {
                playlistInteractor.deletePlaylist(value.playlist)
            }
        }

    }

    private fun getPlaylistLength(tracksList: List<Track>): Int {
        val playlistLengthInMillis: Long = tracksList.sumOf { it.trackTimeMillis }
        return minutesFormat.format(playlistLengthInMillis).toInt()
    }

}