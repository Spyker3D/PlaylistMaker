package com.practicum.playlistmaker.player.presentation

import android.app.Application
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.di.viewModelModule
import com.practicum.playlistmaker.player.domain.entities.AudioPlayerStatesListener
import com.practicum.playlistmaker.player.domain.interactor.AudioPlayerInteractor
import com.practicum.playlistmaker.player.domain.interactor.PlayerState
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

    private val selectedTrack =
        savedStateHandle.get<Track>(KEY_SELECTED_TRACK_DETAILS)!!

    private var trackProgressJob: Job? = null

    private val _isPlaying =
        MutableLiveData<ActivityPlayerState>(ActivityPlayerState.Idle(selectedTrack))
    val isPlaying: LiveData<ActivityPlayerState> = _isPlaying

    private val _progressTimeLiveData = MutableLiveData<String>()
    val progressTimeLiveData: LiveData<String> = _progressTimeLiveData

    private var initialized: Boolean = false

    fun preparePlayer() {

        if (initialized) return

        initialized = true
        audioPlayerInteractor.preparePlayer(
            selectedTrack.previewUrl.toString(),
            object : AudioPlayerStatesListener {
                override fun onPrepared() = Unit

                override fun onCompletion() {
                    _progressTimeLiveData.postValue(getProgressTime())
                    _isPlaying.value = ActivityPlayerState.Idle(selectedTrack)
                }
            })
    }

    fun startOrPause() {
        when (_isPlaying.value) {
            is ActivityPlayerState.IsPlaying -> {
                audioPlayerInteractor.pausePlayer()
                _isPlaying.postValue(ActivityPlayerState.IsPaused(selectedTrack))
            }

            is ActivityPlayerState.Idle, is ActivityPlayerState.IsPaused -> {
                audioPlayerInteractor.startPlayer()
                _isPlaying.postValue(ActivityPlayerState.IsPlaying(selectedTrack))
                startTrackProgressTimer()
            }

            else -> Unit
        }
    }

    private fun startTrackProgressTimer() {
        trackProgressJob = viewModelScope.launch {
            while (audioPlayerInteractor.mediaPlayerIsPlaying()) {
                _progressTimeLiveData.postValue(getProgressTime())
                delay(UPDATE_PLAY_PROGRESS_DEBOUNCE_DELAY)
            }
        }
    }

    fun pausePlayer() {
        audioPlayerInteractor.pausePlayer()
        _isPlaying.postValue(ActivityPlayerState.IsPaused(selectedTrack))
    }

    fun getProgressTime(): String {
        return SimpleDateFormat(
            "m:ss",
            Locale.getDefault()
        ).format(audioPlayerInteractor.getProgressTime())
    }

    override fun onCleared() {
        audioPlayerInteractor.releasePlayer()
    }

    companion object {
        const val UPDATE_PLAY_PROGRESS_DEBOUNCE_DELAY = 300L
    }
}