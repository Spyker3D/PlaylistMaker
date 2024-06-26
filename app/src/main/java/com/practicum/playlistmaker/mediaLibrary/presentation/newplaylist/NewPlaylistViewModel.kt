package com.practicum.playlistmaker.mediaLibrary.presentation.newplaylist

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.mediaLibrary.domain.entities.Playlist
import com.practicum.playlistmaker.mediaLibrary.domain.interactor.PlaylistInteractor
import kotlinx.coroutines.launch

open class NewPlaylistViewModel(
    private val playlistInteractor: PlaylistInteractor,
) : ViewModel() {

    private val _playlistIsCreatedState = MutableLiveData<Boolean>()
    val playlistIsCreatedState: LiveData<Boolean> = _playlistIsCreatedState
    var imageUriInAppStorage: String? = null

    fun savePlaylist(name: String, description: String, imagePath: String) {
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
                        pathToImage = imagePath
                    )
                )
                _playlistIsCreatedState.value = true
            }
        }
    }

    fun saveImageToAppStorage(playlistImage: String, playlistName: String, imagePath: String) {
        viewModelScope.launch {
            playlistInteractor.saveImageToAppStorage(
                playlistImage = playlistImage,
                playlistName = playlistName
            )
            imageUriInAppStorage =
                playlistInteractor.getImagePathToAppStorage(playlistName).toString()
        }
    }

}