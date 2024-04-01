package com.practicum.playlistmaker.domain.entities

interface AudioPlayerStatesListener {

    fun onPrepared()

    fun onCompletion()

}