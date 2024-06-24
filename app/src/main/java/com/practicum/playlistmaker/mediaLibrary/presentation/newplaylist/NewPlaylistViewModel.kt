package com.practicum.playlistmaker.mediaLibrary.presentation.newplaylist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.mediaLibrary.domain.entities.Playlist
import com.practicum.playlistmaker.mediaLibrary.domain.interactor.PlaylistInteractor
import kotlinx.coroutines.launch

class NewPlaylistViewModel(
    private val playlistInteractor: PlaylistInteractor,
) : ViewModel() {

    private val _playlistIsCreatedState = MutableLiveData<Boolean>()
    val playlistIsCreatedState: LiveData<Boolean> = _playlistIsCreatedState

    fun savePlaylist(name: String, description: String, pathToImage: String) {
        viewModelScope.launch {
            val listOfPlaylistsNames = playlistInteractor.getListOfNamesOfAllPlaylists(name)
            if (name in listOfPlaylistsNames) {
                _playlistIsCreatedState.value = false
                return@launch
            } else {
                playlistInteractor.insertPlaylist(
                    Playlist(
                        playlistName = name,
                        playlistDescription = description,
                        pathToImage = pathToImage
                    )
                )
                _playlistIsCreatedState.value = true
            }
        }
    }

    fun saveImageToAppStorage(playlistImage: String, playlistName: String) {
        viewModelScope.launch {
            playlistInteractor.saveImageToAppStorage(
                playlistImage = playlistImage,
                playlistName = playlistName
            )
        }
    }

}