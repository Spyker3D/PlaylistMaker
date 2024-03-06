package com.practicum.playlistmaker.data.repository

import android.media.MediaPlayer
import com.practicum.playlistmaker.domain.entities.AudioPlayerStatesListener
import com.practicum.playlistmaker.domain.repository.AudioPlayerRepository

class AudioPlayerRepositoryImpl() : AudioPlayerRepository {

    private val mediaPlayer = MediaPlayer()

    override fun preparePlayer(trackUrl: String, listener: AudioPlayerStatesListener) {
        mediaPlayer.setDataSource(trackUrl)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener { listener.onPrepared() }
        mediaPlayer.setOnCompletionListener {
            mediaPlayer.seekTo(0)
            listener.onCompletion()
        }
    }

    override fun startPlayer() {
        mediaPlayer.start()
    }

    override fun pausePlayer() {
        mediaPlayer.pause()
    }

    override fun releasePlayer() {
        mediaPlayer.release()
    }

    override fun provideProgressTime(): Int {
        return mediaPlayer.currentPosition
    }
}

