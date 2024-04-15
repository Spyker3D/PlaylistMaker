package com.practicum.playlistmaker.player.presentation

import com.practicum.playlistmaker.search.presentation.entities.Track

sealed class ActivityPlayerState(val track: Track) {
    class IsPlaying(track: Track): ActivityPlayerState(track)

    class IsPaused(track: Track): ActivityPlayerState(track)

    class Idle(track: Track): ActivityPlayerState(track)
}