package com.practicum.playlistmaker.domain.repository

import android.media.MediaPlayer
import com.practicum.playlistmaker.domain.entities.AudioPlayerStatesListener

interface AudioPlayerRepository {

    fun preparePlayer(trackUrl: String, listener: AudioPlayerStatesListener)

    fun startPlayer()

    fun pausePlayer()

    fun releasePlayer()

    fun provideProgressTime(): Int

}