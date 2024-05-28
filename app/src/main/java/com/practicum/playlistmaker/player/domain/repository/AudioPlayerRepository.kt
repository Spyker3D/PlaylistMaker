package com.practicum.playlistmaker.player.domain.repository

import android.text.BoringLayout
import com.practicum.playlistmaker.player.domain.entities.AudioPlayerStatesListener

interface AudioPlayerRepository {

    fun preparePlayer(trackUrl: String, listener: AudioPlayerStatesListener)

    fun startPlayer()

    fun pausePlayer()

    fun releasePlayer()

    fun provideProgressTime(): Int

    fun mediaPlayerIsPlaying(): Boolean

}