package com.practicum.playlistmaker.mediaLibrary.presentation.editplaylist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.mediaLibrary.domain.interactor.PlaylistInteractor
import com.practicum.playlistmaker.mediaLibrary.presentation.newplaylist.NewPlaylistViewModel
import kotlinx.coroutines.launch

class EditPlaylistViewModel(
    private val playlistName: String,
    private val playlistInteractor: PlaylistInteractor,
) : NewPlaylistViewModel(playlistInteractor) {

    private val _playListState = MutableLiveData<EditPlayListState>()
    val playlistState: LiveData<EditPlayListState> = _playListState

    init {
        viewModelScope.launch {
            val playlist = playlistInteractor.getPlaylistByName(playlistName)
            _playListState.value = EditPlayListState(
                playlistNameSecondary = playlist.playlistNameSecondary,
                playlistDescription = playlist.playlistDescription,
                playlistImage = playlist.pathToImage
            )
        }
    }

    fun updatedata() {
        viewModelScope.launch {
            val playlist = playlistInteractor.getPlaylistByName(playlistName)
            _playListState.value = EditPlayListState(
                playlistNameSecondary = playlist.playlistNameSecondary,
                playlistDescription = playlist.playlistDescription,
                playlistImage = playlist.pathToImage
            )
        }
    }

    fun updatePlaylist(
        playlistNameSecondary: String,
        playlistDescriptionNew: String?,
        playlistImageNew: String?,
    ) {
        viewModelScope.launch {
            val initialPlaylist = playlistInteractor.getPlaylistByName(playlistName)

            playlistInteractor.updateExistingPlaylist(
                playlistName,
                playlistNameSecondary,
                playlistDescriptionNew,
                initialPlaylist.numberOfTracks,
                playlistImageNew
            )

        }
    }

}