package com.practicum.playlistmaker.settings.domain.repository


interface SettingsRepository {
    fun getThemeSettings(): Boolean
    fun updateThemeSetting(isNightModeOn: Boolean)
}