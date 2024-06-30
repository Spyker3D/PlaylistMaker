package com.practicum.playlistmaker.mediaLibrary.presentation.editplaylist

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.mediaLibrary.domain.interactor.PlaylistInteractor
import com.practicum.playlistmaker.mediaLibrary.presentation.newplaylist.NewPlaylistViewModel
import com.practicum.playlistmaker.player.presentation.SingleLiveEvent
import kotlinx.coroutines.launch

class EditPlaylistViewModel(
    private val playlistName: String,
    private val playlistInteractor: PlaylistInteractor,
    application: Application
) : NewPlaylistViewModel(playlistInteractor, application) {

    private val _playListState = MutableLiveData<EditPlayListState>()
    val playlistState: LiveData<EditPlayListState> = _playListState
    var numberOfTracks: Int = 0

    private val _closeScreen = SingleLiveEvent<Unit>()
    val closeScreen: LiveData<Unit> = _closeScreen

    init {
        updatedata()
    }

    fun updatedata() {
        viewModelScope.launch {
            val playlist = playlistInteractor.getPlaylistByName(playlistName)
            _playListState.value = EditPlayListState(
                playlistNameSecondary = playlist.playlistNameSecondary,
                playlistDescription = playlist.playlistDescription,
                playlistImage = playlist.pathToImage
            )
            numberOfTracks = playlist.numberOfTracks
        }
    }

    fun updatePlaylist(
        playlistNameSecondary: String,
        playlistDescriptionNew: String?,
        playlistImageNew: Uri?,
    ) {
        viewModelScope.launch {
            playlistInteractor.updateExistingPlaylist(
                playlistName,
                playlistNameSecondary,
                playlistDescriptionNew,
                numberOfTracks,
                imagePath = if (playlistImageNew != null) {
                    playlistInteractor.getImagePathToAppStorage(
                        playlistInteractor.saveImageToAppStorage(
                            playlistImage = playlistImageNew,
                            playlistName = playlistNameSecondary
                        )
                    ).toString()
                } else {
                    _playListState.value?.playlistImage
                }
            )
            _closeScreen.value = Unit
        }
    }

}