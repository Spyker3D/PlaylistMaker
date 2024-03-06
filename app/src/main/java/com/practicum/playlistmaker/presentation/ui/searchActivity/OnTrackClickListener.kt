package com.practicum.playlistmaker.presentation.ui.searchActivity

import com.practicum.playlistmaker.domain.entities.TrackInfo

fun interface OnTrackClickListener {
    fun onTrackClick(track: TrackInfo)
}