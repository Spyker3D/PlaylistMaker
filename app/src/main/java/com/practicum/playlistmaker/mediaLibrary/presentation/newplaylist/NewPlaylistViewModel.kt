package com.practicum.playlistmaker.mediaLibrary.presentation.newplaylist

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.App
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.mediaLibrary.domain.entities.Playlist
import com.practicum.playlistmaker.mediaLibrary.domain.interactor.PlaylistInteractor
import kotlinx.coroutines.launch

open class NewPlaylistViewModel(
    private val playlistInteractor: PlaylistInteractor,
    application: Application
) : AndroidViewModel(application) {

    private val _playlistIsCreatedState = MutableLiveData<NewPlaylistStatus>()
    val playlistIsCreatedState: LiveData<NewPlaylistStatus> = _playlistIsCreatedState

    fun savePlaylistCheck(
        name: String,
        nameSecondary: String,
        description: String,
        imagePath: Uri?,
    ) {
        viewModelScope.launch {
            if (playlistInteractor.isPlaylistAlreadyCreated(nameSecondary)) {
                val message = getApplication<App>().getString(R.string.playlist_name_error_message)
                _playlistIsCreatedState.value =
                    NewPlaylistStatus(isSuccessfullyCreated = false, message = message)

            } else {
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
                val message = getApplication<App>().getString(
                    R.string.playlist_created_message,
                    nameSecondary
                )
                _playlistIsCreatedState.value =
                    NewPlaylistStatus(isSuccessfullyCreated = true, message = message)
            }

        }
    }
}