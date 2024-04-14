package com.practicum.playlistmaker.search.data.storage

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.practicum.playlistmaker.search.data.dto.TrackDto

const val TRACK_LIST_HISTORY_KEY = "savedTrackListHistory"
const val SELECTED_TRACK = "selectedTrackUrl"

class SharedPrefsStorage(private val sharedPreferences: SharedPreferences, private val gson: Gson) :
    TrackStorage {

    override fun saveHistoryTrackList(historyTrackList: List<TrackDto>) {
        val json = gson.toJson(historyTrackList)
        sharedPreferences.edit().putString(TRACK_LIST_HISTORY_KEY, json).apply()
    }

    override fun getHistoryTrackList(): List<TrackDto> {
        val json = sharedPreferences.getString(TRACK_LIST_HISTORY_KEY, null)
            ?: return arrayListOf<TrackDto>()
        return gson.fromJson(json, Array<TrackDto>::class.java).toMutableList()
    }

    override fun saveSelectedTrack(selectedTrack: TrackDto) {
        val json = gson.toJson(selectedTrack)
        sharedPreferences.edit().putString(SELECTED_TRACK, json).apply()
    }
}