package com.practicum.playlistmaker.player.domain.repository

import com.practicum.playlistmaker.player.domain.entities.AudioPlayerStatesListener

interface AudioPlayerRepository {

    fun preparePlayer(trackUrl: String, listener: AudioPlayerStatesListener)

    fun startPlayer()

    fun pausePlayer()

    fun releasePlayer()

    fun provideProgressTime(): Int

}