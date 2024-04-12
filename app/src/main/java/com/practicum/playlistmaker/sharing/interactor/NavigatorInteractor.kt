package com.practicum.playlistmaker.sharing.interactor

import com.practicum.playlistmaker.sharing.domain.externalNavigator.NavigatorRepository

class NavigatorInteractor(private val navigatorRepository: NavigatorRepository) {

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