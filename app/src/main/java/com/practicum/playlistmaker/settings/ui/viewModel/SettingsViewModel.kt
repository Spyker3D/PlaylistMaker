package com.practicum.playlistmaker.settings.ui.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.practicum.playlistmaker.creator.Creator

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    companion object {

        fun getViewModelFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                SettingsViewModel(this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as Application)
            }
        }
    }

    private val navigatorRepository by lazy { Creator.provideNavigatorRepository() }
    private val settingsRepository by lazy { Creator.provideSettingsRepository() }

    fun switchTheme(isNightModeOn: Boolean) {
        settingsRepository.updateThemeSetting(isNightModeOn)
    }

    fun getIsNightModeOn(): Boolean {
        return settingsRepository.getThemeSettings()
    }

    fun shareApp() {
        navigatorRepository.shareApp()
    }

    fun openTerms() {
        navigatorRepository.openTerms()
    }

    fun openEmail() {
        navigatorRepository.openEmail()
    }
}

