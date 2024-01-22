package com.practicum.playlistmaker

import android.app.Application
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate

const val PLAYLISTMAKER_SHARED_PREFS = "com.practicum.playlistmaker.MY_PREFS"
const val SWITCHER_IS_CHECKED_STATUS = "isNightModeOn"

class App : Application() {

    var nightMode = false
    override fun onCreate() {
        super.onCreate()
        nightMode = getSharedPreferences(PLAYLISTMAKER_SHARED_PREFS, MODE_PRIVATE).getBoolean(
            SWITCHER_IS_CHECKED_STATUS, false
        )
        switchTheme(nightMode)
    }

    fun switchTheme(isNightModeOn: Boolean) {
        nightMode = isNightModeOn
        AppCompatDelegate.setDefaultNightMode(
            if (isNightModeOn) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
        getSharedPreferences(PLAYLISTMAKER_SHARED_PREFS, MODE_PRIVATE)
            .edit()
            .putBoolean(SWITCHER_IS_CHECKED_STATUS, isNightModeOn)
            .apply()
    }
}