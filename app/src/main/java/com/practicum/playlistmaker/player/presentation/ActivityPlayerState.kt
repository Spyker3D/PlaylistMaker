package com.practicum.playlistmaker.player.presentation

import com.practicum.playlistmaker.search.presentation.entities.Track

sealed class ActivityPlayerState(val track: Track) {
    class Playing(track: Track): ActivityPlayerState(track)

    class Paused(track: Track): ActivityPlayerState(track) // плеер готов, но не играет

    class Idle(track: Track): ActivityPlayerState(track) // плеер не готов

}