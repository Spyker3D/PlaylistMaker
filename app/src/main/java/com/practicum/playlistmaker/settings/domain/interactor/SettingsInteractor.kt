package com.practicum.playlistmaker.settings.domain.interactor

import com.practicum.playlistmaker.settings.domain.repository.SettingsRepository

class SettingsInteractor(private val settingsRepository: SettingsRepository) {
    fun getThemeSettings(): Boolean {
        return settingsRepository.getThemeSettings()
    }
    fun updateThemeSetting(isNightModeOn: Boolean) {
        settingsRepository.updateThemeSetting(isNightModeOn)
    }
}