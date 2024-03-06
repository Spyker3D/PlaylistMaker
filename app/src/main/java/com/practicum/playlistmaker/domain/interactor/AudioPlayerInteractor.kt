package com.practicum.playlistmaker.domain.interactor

import com.practicum.playlistmaker.domain.entities.AudioPlayerStatesListener
import com.practicum.playlistmaker.domain.repository.AudioPlayerRepository

class AudioPlayerInteractor(private val audioPlayerRepository: AudioPlayerRepository) {

    var audioPlayerState: PlayerState = PlayerState.STATE_DEFAULT // добавить метод
        private set


    fun preparePlayer(trackUrl: String, listener: AudioPlayerStatesListener) {
        audioPlayerState = PlayerState.STATE_PREPARED
        audioPlayerRepository.preparePlayer(trackUrl, listener = object : AudioPlayerStatesListener {
            override fun onPrepared() {
                listener.onPrepared()
            }

            override fun onCompletion() {
                audioPlayerState = PlayerState.STATE_PREPARED
                listener.onCompletion()
            }
        })
    }

    fun startPlayer() {
        audioPlayerState = PlayerState.STATE_PLAYING
        audioPlayerRepository.startPlayer()
    }

    fun pausePlayer() {
        audioPlayerState = PlayerState.STATE_PAUSED
        audioPlayerRepository.pausePlayer()
    }

    fun releasePlayer() {
        audioPlayerRepository.releasePlayer()
    }

    fun getProgressTime(): Int {
        return audioPlayerRepository.provideProgressTime()
    }
}

