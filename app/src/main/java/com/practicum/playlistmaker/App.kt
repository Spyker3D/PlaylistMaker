package com.practicum.playlistmaker

import android.app.Application
import com.practicum.playlistmaker.creator.Creator

class App : Application() {

    var nightMode = false
    override fun onCreate() {
        super.onCreate()

        Creator.initApp(this)
        val settingsRepository = Creator.provideSettingsRepository()

        nightMode = settingsRepository.getThemeSettings()

        settingsRepository.updateThemeSetting(nightMode)
    }
}