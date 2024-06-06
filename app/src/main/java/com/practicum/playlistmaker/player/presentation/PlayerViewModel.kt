package com.practicum.playlistmaker.player.presentation

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.player.domain.entities.AudioPlayerStatesListener
import com.practicum.playlistmaker.player.domain.interactor.AudioPlayerInteractor
import com.practicum.playlistmaker.search.presentation.entities.Track
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerViewModel(
    savedStateHandle: SavedStateHandle,
    application: Application,
    private val audioPlayerInteractor: AudioPlayerInteractor,
) :
    AndroidViewModel(application) {

    private val dateFormat by lazy {
        SimpleDateFormat("m:ss", Locale.getDefault())
    }

    private val selectedTrack =
        savedStateHandle.get<Track>(KEY_SELECTED_TRACK_DETAILS)!!

    private var trackProgressJob: Job? = null

    private val _playerState =
        MutableLiveData<ActivityPlayerState>(ActivityPlayerState.Idle(selectedTrack))
    val playerState: LiveData<ActivityPlayerState> = _playerState

    private val _progressTimeLiveData = MutableLiveData<String>()
    val progressTimeLiveData: LiveData<String> = _progressTimeLiveData

    private var initialized: Boolean = false

    fun preparePlayer() {

        if (initialized) return

        initialized = true
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

    fun getProgressTime(): String {
        return dateFormat.format(audioPlayerInteractor.getProgressTime())
    }

    override fun onCleared() {
        audioPlayerInteractor.releasePlayer()
    }

    companion object {
        const val UPDATE_PLAY_PROGRESS_DEBOUNCE_DELAY = 300L
    }
}