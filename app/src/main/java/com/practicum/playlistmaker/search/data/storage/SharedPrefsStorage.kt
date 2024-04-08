package com.practicum.playlistmaker.search.data.storage

import android.content.Context
import com.google.gson.Gson
import com.practicum.playlistmaker.search.data.dto.TrackDto

const val PLAYLISTMAKER_SHARED_PREFS = "com.practicum.playlistmaker.MY_PREFS"
const val TRACK_LIST_HISTORY_KEY = "savedTrackListHistory"
const val SELECTED_TRACK = "selectedTrackUrl"

class SharedPrefsStorage(context: Context) : TrackStorage {

    private val sharedPreferences = context.getSharedPreferences(
        PLAYLISTMAKER_SHARED_PREFS,
        Context.MODE_PRIVATE
    )

    override fun saveHistoryTrackList(historyTrackList: List<TrackDto>) {
        val json = Gson().toJson(historyTrackList)
        sharedPreferences.edit().putString(TRACK_LIST_HISTORY_KEY, json).apply()
    }

    override fun getHistoryTrackList(): List<TrackDto> {
        val json = sharedPreferences.getString(TRACK_LIST_HISTORY_KEY, null)
            ?: return arrayListOf<TrackDto>()
        return Gson().fromJson(json, Array<TrackDto>::class.java).toMutableList()
    }

    override fun saveSelectedTrack(selectedTrack: TrackDto) {
        val json = Gson().toJson(selectedTrack)
        sharedPreferences.edit().putString(SELECTED_TRACK, json).apply()
    }

    override fun getSelectedTrack(): TrackDto {
        val json = sharedPreferences.getString(SELECTED_TRACK, null)
        return Gson().fromJson(json, TrackDto::class.java)
    }
}