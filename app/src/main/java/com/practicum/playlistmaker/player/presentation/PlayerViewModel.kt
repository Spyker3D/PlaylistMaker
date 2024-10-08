package com.practicum.playlistmaker.player.presentation

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.App
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.mediaLibrary.domain.entities.Playlist
import com.practicum.playlistmaker.mediaLibrary.domain.interactor.FavouriteTracksInteractor
import com.practicum.playlistmaker.mediaLibrary.domain.interactor.PlaylistInteractor
import com.practicum.playlistmaker.mediaLibrary.presentation.playlists.PlaylistsState
import com.practicum.playlistmaker.player.domain.entities.AudioPlayerStatesListener
import com.practicum.playlistmaker.player.domain.interactor.AudioPlayerInteractor
import com.practicum.playlistmaker.search.presentation.entities.Track
import com.practicum.playlistmaker.search.presentation.mapper.TrackPresentationMapper.mapToDomain
import com.practicum.playlistmaker.search.presentation.mapper.TrackPresentationMapper.mapToPresentation
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerViewModel(
    savedStateHandle: SavedStateHandle,
    application: Application,
    private val audioPlayerInteractor: AudioPlayerInteractor,
    private val favouriteTracksInteractor: FavouriteTracksInteractor,
    private val playlistsInteractor: PlaylistInteractor,
) :
    AndroidViewModel(application) {

    private val dateFormat by lazy {
        SimpleDateFormat("m:ss", Locale.getDefault())
    }

    private var selectedTrack = savedStateHandle.get<Track>(KEY_SELECTED_TRACK_DETAILS)!!

    private var trackProgressJob: Job? = null

    private val _playerState =
        MutableLiveData<ActivityPlayerState>(ActivityPlayerState.Idle(selectedTrack))
    val playerState: LiveData<ActivityPlayerState> = _playerState

    private val _progressTimeLiveData = MutableLiveData<String>()
    val progressTimeLiveData: LiveData<String> = _progressTimeLiveData

    private val _isFavourite = MutableLiveData<Boolean>()
    val isFavourite: LiveData<Boolean> = _isFavourite

    private val _playlistsState =
        MutableLiveData<PlaylistsState>(PlaylistsState.Empty)
    val playlistsState: LiveData<PlaylistsState> = _playlistsState

    private val _trackAddToastState = SingleLiveEvent<String>()
    val trackAddToastState: LiveData<String> = _trackAddToastState

    private var initialized: Boolean = false
    private var isFavouriteJob: Job? = null

    init {
        viewModelScope.launch {
            playlistsInteractor.getAllPlaylists().collect {
                processResult(it)
            }
        }
    }

    fun preparePlayer() {

        if (initialized) return

        initialized = true

        viewModelScope.launch {
            val trackInfo = selectedTrack.mapToDomain()
            favouriteTracksInteractor.updateTrackStatus(trackInfo)
            val updatedSelectedTrack = trackInfo.mapToPresentation()
            _isFavourite.value = updatedSelectedTrack.isFavorite
            selectedTrack = updatedSelectedTrack
            _playerState.value = ActivityPlayerState.Idle(updatedSelectedTrack)

            audioPlayerInteractor.preparePlayer(
                selectedTrack.previewUrl.toString(),
                object : AudioPlayerStatesListener {
                    override fun onPrepared() {
                        _playerState.value = ActivityPlayerState.Paused(selectedTrack)
                    }

                    override fun onCompletion() {
                        _progressTimeLiveData.postValue(getProgressTime())
                        _playerState.value = ActivityPlayerState.Paused(selectedTrack)
                    }
                })
        }
    }

    fun startOrPause() {
        when (_playerState.value) {
            is ActivityPlayerState.Playing -> {
                audioPlayerInteractor.pausePlayer()
                _playerState.value = ActivityPlayerState.Paused(selectedTrack)
            }

            is ActivityPlayerState.Idle, is ActivityPlayerState.Paused -> {
                audioPlayerInteractor.startPlayer()
                _playerState.value = ActivityPlayerState.Playing(selectedTrack)
                startTrackProgressTimer()
            }

            else -> Unit
        }
    }

    private fun startTrackProgressTimer() {
        trackProgressJob?.cancel()
        trackProgressJob = viewModelScope.launch {
            while (audioPlayerInteractor.mediaPlayerIsPlaying()) {
                _progressTimeLiveData.postValue(getProgressTime())
                delay(UPDATE_PLAY_PROGRESS_DEBOUNCE_DELAY)
            }
        }
    }

    fun pausePlayer() {
        audioPlayerInteractor.pausePlayer()
        _playerState.value = ActivityPlayerState.Paused(selectedTrack)
    }

    private fun getProgressTime(): String {
        return dateFormat.format(audioPlayerInteractor.getProgressTime())
    }

    fun onFavouriteClicked() {
        if (isFavouriteJob?.isActive == true) return
        val track = _playerState.value?.track ?: return

        isFavouriteJob = viewModelScope.launch {
            val isInFavouriteList = favouriteTracksInteractor.isFavourite(track.mapToDomain())
            if (isInFavouriteList) {
                favouriteTracksInteractor.deleteFromFavouriteTracks(track.mapToDomain())
                _isFavourite.value = false
            } else {
                favouriteTracksInteractor.insertToFavouriteTracks(track.mapToDomain())
                _isFavourite.value = true
            }
        }
//        isFavouriteJob = viewModelScope.launch {
//            val trackInfo = track.mapToDomain()
//
//            if (track.isFavorite) {
//                favouriteTracksInteractor.deleteFromFavouriteTracks(trackInfo)
//                track.isFavorite = false
//            } else {
//                favouriteTracksInteractor.insertToFavouriteTracks(trackInfo)
//                track.isFavorite = true
//            }
//
//            _isFavourite.value = track.isFavorite
//        }
    }

    override fun onCleared() {
        audioPlayerInteractor.releasePlayer()
    }

    fun addTrackToPlaylist(playlist: Playlist) {
        viewModelScope.launch {
            val trackIsAddedResult: Boolean =
                playlistsInteractor.addTrackToPlaylist(selectedTrack, playlist)
            if (trackIsAddedResult) {
                _trackAddToastState.value =
                    getApplication<App>().getString(
                        R.string.track_successfully_added_to_playlist,
                        playlist.playlistNameSecondary
                    )
            } else {
                _trackAddToastState.value =
                    getApplication<App>().getString(
                        R.string.track_insert_to_playlist_error,
                        playlist.playlistNameSecondary
                    )
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

    companion object {
        const val UPDATE_PLAY_PROGRESS_DEBOUNCE_DELAY = 300L
    }
}