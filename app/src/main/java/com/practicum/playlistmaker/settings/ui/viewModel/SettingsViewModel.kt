package com.practicum.playlistmaker.settings.ui.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.practicum.playlistmaker.App
import com.practicum.playlistmaker.creator.Creator
import com.practicum.playlistmaker.settings.domain.interactor.SettingsInteractor
import com.practicum.playlistmaker.sharing.interactor.NavigatorInteractor

class SettingsViewModel(
    application: Application,
    private val navigatorInteractor: NavigatorInteractor,
    private val settingsInteractor: SettingsInteractor,
) : AndroidViewModel(application) {

    companion object {

        fun getViewModelFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {

                SettingsViewModel(
                    application = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as Application,
                    navigatorInteractor = Creator.provideNavigatorInteractor(),
                    settingsInteractor = Creator.provideSettingsInteractor()
                )
            }
        }
    }

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

