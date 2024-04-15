package com.practicum.playlistmaker.settings.presentation

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.practicum.playlistmaker.settings.domain.interactor.SettingsInteractor
import com.practicum.playlistmaker.sharing.interactor.NavigatorInteractor

class SettingsViewModel(
    application: Application,
    private val navigatorInteractor: NavigatorInteractor,
    private val settingsInteractor: SettingsInteractor,
) : AndroidViewModel(application) {

    private var isNightModeOnMutableLiveData = MutableLiveData<Boolean>()
    val isNightModeOnLiveData: LiveData<Boolean> = isNightModeOnMutableLiveData

    init {
        getIsNightModeOn()
    }

    fun switchTheme(isNightModeOn: Boolean) {
        settingsInteractor.updateThemeSetting(isNightModeOn)
        isNightModeOnMutableLiveData.value = isNightModeOn
    }

    private fun getIsNightModeOn() {
        isNightModeOnMutableLiveData.value = settingsInteractor.getThemeSettings()
    }

    fun shareApp() {
        navigatorInteractor.shareApp()
    }

    fun openTerms() {
        navigatorInteractor.openTerms()
    }

    fun openEmail() {
        navigatorInteractor.openEmail()
    }
}

