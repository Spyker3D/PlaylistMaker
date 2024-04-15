package com.practicum.playlistmaker.player.presentation

import android.app.Application
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import com.practicum.playlistmaker.player.domain.entities.AudioPlayerStatesListener
import com.practicum.playlistmaker.player.domain.interactor.AudioPlayerInteractor
import com.practicum.playlistmaker.player.domain.interactor.PlayerState
import com.practicum.playlistmaker.search.presentation.entities.Track

class PlayerViewModel(
    savedStateHandle: SavedStateHandle,
    application: Application,
    private val audioPlayerInteractor: AudioPlayerInteractor,
) :
    AndroidViewModel(application) {

    private val selectedTrack =
        savedStateHandle.get<Track>(KEY_SELECTED_TRACK_DETAILS)!!

    private val _isPlaying =
        MutableLiveData<ActivityPlayerState>(ActivityPlayerState.Idle(selectedTrack))
    val isPlaying: LiveData<ActivityPlayerState> = _isPlaying

    private val progressTimeLiveDataMutable = MutableLiveData<Int>()
    val progressTimeLiveData: LiveData<Int> = progressTimeLiveDataMutable

    private var initialized: Boolean = false

    private val handler = Handler(Looper.getMainLooper())

    private val replayProgressRunnable = object : Runnable {
        override fun run() {
            progressTimeLiveDataMutable.postValue(getProgressTime())
            handler.postDelayed(this, UPDATE_PLAY_PROGRESS_DEBOUNCE_DELAY)
        }
    }

    fun preparePlayer() {

        if (initialized) return

        initialized = true
        audioPlayerInteractor.preparePlayer(
            selectedTrack.previewUrl.toString(),
            object : AudioPlayerStatesListener {
                override fun onPrepared() = Unit

                override fun onCompletion() {
                    handler.removeCallbacks(replayProgressRunnable)
                    progressTimeLiveDataMutable.postValue(getProgressTime())
                    _isPlaying.value = ActivityPlayerState.Idle(selectedTrack)
                }
            })
    }

    fun startOrPause() {
        when (audioPlayerInteractor.audioPlayerState) {
            PlayerState.STATE_PLAYING -> {
                audioPlayerInteractor.pausePlayer()
                handler.removeCallbacks(replayProgressRunnable)
                _isPlaying.postValue(ActivityPlayerState.IsPaused(selectedTrack))
            }

            PlayerState.STATE_PREPARED, PlayerState.STATE_PAUSED -> {
                audioPlayerInteractor.startPlayer()
                handler.post(replayProgressRunnable)
                _isPlaying.postValue(ActivityPlayerState.IsPlaying(selectedTrack))
            }

            PlayerState.STATE_DEFAULT -> Unit
        }
    }

    fun pausePlayer() {
        audioPlayerInteractor.pausePlayer()
        handler.removeCallbacks(replayProgressRunnable)
        _isPlaying.postValue(ActivityPlayerState.IsPaused(selectedTrack))
    }

    fun getProgressTime(): Int {
        return audioPlayerInteractor.getProgressTime()
    }

    override fun onCleared() {
        audioPlayerInteractor.releasePlayer()
        handler.removeCallbacks(replayProgressRunnable)
    }

    companion object {
        const val UPDATE_PLAY_PROGRESS_DEBOUNCE_DELAY = 300L
    }
}