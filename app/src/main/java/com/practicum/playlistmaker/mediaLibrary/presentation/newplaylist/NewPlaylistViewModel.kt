package com.practicum.playlistmaker.mediaLibrary.presentation.newplaylist

import android.net.Uri
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

//    private val _playlistIsCreatedState = MutableLiveData<Boolean>()
//    val playlistIsCreatedState: LiveData<Boolean> = _playlistIsCreatedState

    fun savePlaylistCheck(
        name: String,
        nameSecondary: String,
        description: String,
        imagePath: Uri?,
    ) {
        viewModelScope.launch {
//            val listOfPlaylistsNames = playlistInteractor.getListOfNamesOfAllPlaylists(name)
//            if (name in listOfPlaylistsNames) {
//                _playlistIsCreatedState.value = false
//                return@launch
//            } else {
//                _playlistIsCreatedState.value = true
//                savePlaylist(
//                    playlistImage = imagePath,
//                    playlistName = name,
//                    playlistNameSecondary = nameSecondary,
//                    playlistDescription = description
//                )
//            }

//            if (imagePath != null) {
//                playlistInteractor.saveImageToAppStorage(
//                    playlistImage = imagePath,
//                    playlistName = name
//                )
//            }
//            val imageUriInAppStorage =
//                playlistInteractor.getImagePathToAppStorage(imagePath).toString()

            playlistInteractor.insertPlaylist(
                Playlist(
                    playlistName = name,
                    playlistNameSecondary = nameSecondary,
                    playlistDescription = description,
                    pathToImage = if (imagePath != null) {
                        playlistInteractor.getImagePathToAppStorage(
                            playlistInteractor.saveImageToAppStorage(
                                playlistImage = imagePath,
                                playlistName = name
                            )
                        ).toString()
                    } else {
                        null
                    }
                )
            )
        }

//        fun savePlaylist(
//            playlistImage: String,
//            playlistName: String,
//            playlistNameSecondary: String,
//            playlistDescription: String,
//        ) {
//            viewModelScope.launch {
//                playlistInteractor.saveImageToAppStorage(
//                    playlistImage = playlistImage,
//                    playlistName = playlistName
//                )
//                val imageUriInAppStorage =
//                    playlistInteractor.getImagePathToAppStorage(playlistName).toString()
//
//                playlistInteractor.insertPlaylist(
//                    Playlist(
//                        playlistName = playlistName,
//                        playlistNameSecondary = playlistNameSecondary,
//                        playlistDescription = playlistDescription,
//                        pathToImage = imageUriInAppStorage
//                    )
//                )
//            }
//        }
    }
}