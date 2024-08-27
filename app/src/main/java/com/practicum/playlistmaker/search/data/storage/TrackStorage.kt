package com.practicum.playlistmaker.search.data.storage

import com.practicum.playlistmaker.search.data.dto.TrackDto

interface TrackStorage {

    fun  saveHistoryTrackList(historyTrackList: List<TrackDto>)

    fun getHistoryTrackList(): List<TrackDto>

}