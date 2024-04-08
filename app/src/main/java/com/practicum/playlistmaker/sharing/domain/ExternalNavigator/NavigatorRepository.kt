package com.practicum.playlistmaker.sharing.domain.ExternalNavigator


interface NavigatorRepository {

    fun shareApp()

    fun openTerms()

    fun openEmail()
}