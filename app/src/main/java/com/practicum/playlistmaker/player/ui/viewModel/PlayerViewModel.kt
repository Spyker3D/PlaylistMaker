package com.practicum.playlistmaker.player.ui.viewModel

import android.app.Application
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.practicum.playlistmaker.creator.Creator
import com.practicum.playlistmaker.player.domain.entities.AudioPlayerStatesListener
import com.practicum.playlistmaker.player.domain.interactor.AudioPlayerInteractor
import com.practicum.playlistmaker.player.domain.interactor.PlayerState
import com.practicum.playlistmaker.search.domain.entities.TrackInfo
import com.practicum.playlistmaker.search.ui.entities.Track

class PlayerViewModel(
    application: Application,
    private val selectedTrack: Track,
    private val audioPlayerInteractor: AudioPlayerInteractor
) :
    AndroidViewModel(application) {

    companion object {
        const val UPDATE_PLAY_PROGRESS_DEBOUNCE_DELAY = 300L

        fun getViewModelFactory(selectedTrack: Track): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                PlayerViewModel(
                    application = this[APPLICATION_KEY] as Application,
                    selectedTrack = selectedTrack,
                    audioPlayerInteractor = Creator.provideAudioPlayerInteractor()
                )
            }
        }
    }

    private val isPlayingMutable = MutableLiveData<Boolean>(false) // Boolean
    val isPlaying: LiveData<Boolean> = isPlayingMutable

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

    fun preparePlayer(listener: AudioPlayerStatesListener) {

        if (initialized) return

        initialized = true
        audioPlayerInteractor.preparePlayer(
            selectedTrack.previewUrl.toString(),
            object : AudioPlayerStatesListener {
                override fun onPrepared() {
                    listener.onPrepared()
                }

                override fun onCompletion() {
                    handler.removeCallbacks(replayProgressRunnable)
                    progressTimeLiveDataMutable.postValue(getProgressTime())
                    listener.onCompletion()
                }
            })
    }

    fun startOrPause() {
        when (audioPlayerInteractor.audioPlayerState) {
            PlayerState.STATE_PLAYING -> {
                audioPlayerInteractor.pausePlayer()
                handler.removeCallbacks(replayProgressRunnable)
                isPlayingMutable.postValue(false)
            }

            PlayerState.STATE_PREPARED, PlayerState.STATE_PAUSED -> {
                audioPlayerInteractor.startPlayer()
                handler.post(replayProgressRunnable)
                isPlayingMutable.postValue(true)
            }

            PlayerState.STATE_DEFAULT -> Unit
        }
    }

    fun pausePlayer() {
        audioPlayerInteractor.pausePlayer()
        handler.removeCallbacks(replayProgressRunnable)
        isPlayingMutable.postValue(false)
    }

    fun getProgressTime(): Int {
        return audioPlayerInteractor.getProgressTime()
    }

    override fun onCleared() {
        audioPlayerInteractor.releasePlayer()
        handler.removeCallbacks(replayProgressRunnable)
    }
}