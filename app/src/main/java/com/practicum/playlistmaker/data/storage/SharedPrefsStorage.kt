package com.practicum.playlistmaker.data.storage

import android.content.Context
import com.google.gson.Gson
import com.practicum.playlistmaker.data.dto.TrackDto

const val PLAYLISTMAKER_SHARED_PREFS = "com.practicum.playlistmaker.MY_PREFS"
const val TRACK_LIST_HISTORY_KEY = "savedTrackListHistory"

class SharedPrefsStorage(context: Context) : TrackStorage {

    private val sharedPreferences = context.getSharedPreferences(
        PLAYLISTMAKER_SHARED_PREFS,
        Context.MODE_PRIVATE
    )

    override fun saveHistoryTrackList(historyTrackList: List<TrackDto>) {
        val json = Gson().toJson(historyTrackList)
        sharedPreferences.edit()
            .putString(TRACK_LIST_HISTORY_KEY, json)
            .apply()
    }

    override fun getHistoryTrackList(): List<TrackDto> {
        val json = sharedPreferences.getString(TRACK_LIST_HISTORY_KEY, null) ?: return arrayListOf()
        return Gson().fromJson(json, Array<TrackDto>::class.java).toMutableList()
    }
}