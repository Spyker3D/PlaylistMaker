package com.practicum.playlistmaker.player.domain.entities

interface AudioPlayerStatesListener {

    fun onPrepared()

    fun onCompletion()

}