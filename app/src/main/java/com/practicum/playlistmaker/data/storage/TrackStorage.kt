package com.practicum.playlistmaker.data.storage

import com.practicum.playlistmaker.data.dto.TrackDto

interface TrackStorage {

    fun saveHistoryTrackList(historyTrackList: List<TrackDto>)

    fun getHistoryTrackList(): List<TrackDto>
}