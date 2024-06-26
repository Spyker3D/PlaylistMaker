package com.practicum.playlistmaker

import android.app.Application
import com.markodevcic.peko.PermissionRequester
import com.practicum.playlistmaker.di.dataModule
import com.practicum.playlistmaker.di.interactorModule
import com.practicum.playlistmaker.di.repositoryModule
import com.practicum.playlistmaker.di.viewModelModule
import com.practicum.playlistmaker.settings.domain.interactor.SettingsInteractor
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App : Application() {

    private var nightMode = false

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@App)
            modules(dataModule, interactorModule, repositoryModule, viewModelModule)
        }
        val settingsInteractor: SettingsInteractor by inject()
        nightMode = settingsInteractor.getThemeSettings()
        settingsInteractor.updateThemeSetting(nightMode)

        PermissionRequester.initialize(applicationContext) // Initialization of Peko library
    }
}