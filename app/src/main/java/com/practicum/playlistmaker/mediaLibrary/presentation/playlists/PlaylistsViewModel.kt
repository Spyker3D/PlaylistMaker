package com.practicum.playlistmaker.mediaLibrary.presentation.playlists

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.mediaLibrary.domain.entities.Playlist
import com.practicum.playlistmaker.mediaLibrary.domain.interactor.PlaylistInteractor
import com.practicum.playlistmaker.mediaLibrary.domain.repository.PlaylistsRepository
import kotlinx.coroutines.launch

class PlaylistsViewModel(
    private val playlistInteractor: PlaylistInteractor
): ViewModel() {

    private val _playlistsState =
        MutableLiveData<PlaylistsState>(PlaylistsState.Empty)
    val playlistsState: LiveData<PlaylistsState> = _playlistsState

    fun loadAllPlaylists() {
        viewModelScope.launch {
            playlistInteractor.getAllPlaylists().collect {
                processResult(it)
            }
        }
    }

    private fun processResult(playlistsResult: List<Playlist>) {
        when {
            playlistsResult.isEmpty() -> {
                _playlistsState.value = PlaylistsState.Empty
            }

            else -> {
                _playlistsState.value = PlaylistsState.Content(playlistsResult)
            }
        }
    }

}
