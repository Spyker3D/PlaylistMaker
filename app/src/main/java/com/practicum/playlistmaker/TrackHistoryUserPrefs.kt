package com.practicum.playlistmaker

import android.content.SharedPreferences
import com.google.gson.Gson

const val TRACK_LIST_HISTORY_KEY = "savedTrackListHistory"

fun writeTrackListHistoryToSharedPrefs(
    sharedPreferences: SharedPreferences,
    trackListForHistory: List<Track>
) {
    val json = Gson().toJson(trackListForHistory)
    sharedPreferences.edit()
        .putString(TRACK_LIST_HISTORY_KEY, json)
        .apply()
}

fun readFromTrackListHistoryFromSharedPrefs(sharedPreferences: SharedPreferences): Array<Track> {
    val json = sharedPreferences.getString(TRACK_LIST_HISTORY_KEY, null) ?: return emptyArray()
    return Gson().fromJson(json, Array<Track>::class.java)
}

fun addToHistoryList(track: Track, historyTrackList: List<Track>): List<Track> {
    val filteredList = historyTrackList - track
    val updatedList = listOf(track) + filteredList
    return if (updatedList.size > 10) updatedList.subList(0, 10) else updatedList
}

fun historyAdapterRestore(
    sharedPreferences: SharedPreferences,
    key: String?,
    adapter: TrackAdapter
) {
    if (key == TRACK_LIST_HISTORY_KEY) {
        val trackListAsArrayList =
            readFromTrackListHistoryFromSharedPrefs(sharedPreferences).toCollection(ArrayList())
        adapter.updateList {
            trackListAsArrayList
        }
    }
}



