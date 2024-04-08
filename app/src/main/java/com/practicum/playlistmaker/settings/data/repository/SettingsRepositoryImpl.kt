package com.practicum.playlistmaker.settings.data.repository

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import com.practicum.playlistmaker.settings.domain.repository.SettingsRepository

const val THEME_KEY = "APP_THEME_KEY"

class SettingsRepositoryImpl(private val sharedPreferences: SharedPreferences) :
    SettingsRepository {
    override fun getThemeSettings(): Boolean {
        return sharedPreferences.getBoolean(THEME_KEY, false)
    }

    override fun updateThemeSetting(isNightModeOn: Boolean) {

        AppCompatDelegate.setDefaultNightMode(
            if (isNightModeOn) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )

        sharedPreferences.edit().putBoolean(THEME_KEY, isNightModeOn).apply()
    }
}