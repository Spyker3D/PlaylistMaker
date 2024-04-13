package com.practicum.playlistmaker.creator

import android.app.Application
import android.content.Context
import com.practicum.playlistmaker.App
import com.practicum.playlistmaker.search.data.network.NetworkClient
import com.practicum.playlistmaker.search.data.network.RetrofitNetworkClient
import com.practicum.playlistmaker.player.data.repository.AudioPlayerRepositoryImpl
import com.practicum.playlistmaker.search.data.repository.TrackSearchRepositoryImpl
import com.practicum.playlistmaker.search.data.repository.TrackStorageRepositoryImpl
import com.practicum.playlistmaker.search.data.storage.SharedPrefsStorage
import com.practicum.playlistmaker.search.data.storage.TrackStorage
import com.practicum.playlistmaker.player.domain.repository.AudioPlayerRepository
import com.practicum.playlistmaker.player.domain.interactor.AudioPlayerInteractor
import com.practicum.playlistmaker.search.domain.interactor.GetHistoryTrackUseCase
import com.practicum.playlistmaker.search.domain.interactor.SaveHistoryTrackUseCase
import com.practicum.playlistmaker.search.domain.interactor.SearchTrackUseCase
import com.practicum.playlistmaker.search.domain.interactor.SaveSelectedTrackUseCase
import com.practicum.playlistmaker.search.domain.repository.TrackSearchRepository
import com.practicum.playlistmaker.search.domain.repository.TrackStorageRepository
import com.practicum.playlistmaker.settings.data.repository.SettingsRepositoryImpl
import com.practicum.playlistmaker.settings.domain.interactor.SettingsInteractor
import com.practicum.playlistmaker.settings.domain.repository.SettingsRepository
import com.practicum.playlistmaker.sharing.data.externalNavigator.ExternalNavigator
import com.practicum.playlistmaker.sharing.data.repository.NavigatorRepositoryImpl
import com.practicum.playlistmaker.sharing.domain.externalNavigator.NavigatorRepository
import com.practicum.playlistmaker.sharing.interactor.NavigatorInteractor

const val PLAYLISTMAKER_SHARED_PREFS = "com.practicum.playlistmaker.MY_PREFS"

object Creator {

    private lateinit var app: Application

    fun initApp(app: App) {
        this.app = app
    }

    fun provideSearchTrackUseCase(): SearchTrackUseCase {
        return SearchTrackUseCase(trackSearchRepository = provideTrackSearchRepository())
    }

    private fun provideTrackSearchRepository(): TrackSearchRepository {
        return TrackSearchRepositoryImpl(networkClient = provideNetworkClient())
    }

    private fun provideNetworkClient(): NetworkClient {
        return RetrofitNetworkClient(app)
    }

    fun provideSaveHistoryTrackUseCase(): SaveHistoryTrackUseCase {
        return SaveHistoryTrackUseCase(trackStorage = provideStorageRepository())
    }

    private fun provideStorageRepository(): TrackStorageRepository {
        return TrackStorageRepositoryImpl(trackStorage = provideTrackStorage())
    }

    private fun provideTrackStorage(): TrackStorage {
        return SharedPrefsStorage(app)
    }

    fun provideGetHistoryTrackUseCase(): GetHistoryTrackUseCase {
        return GetHistoryTrackUseCase(trackStorage = provideStorageRepository())
    }

    fun provideSelectedTrackInteractor(): SaveSelectedTrackUseCase {
        return SaveSelectedTrackUseCase(trackStorage = provideStorageRepository())
    }

    fun provideAudioPlayerInteractor(): AudioPlayerInteractor {
        return AudioPlayerInteractor(audioPlayerRepository = provideDataAudioPlayerRepository())
    }

    private fun provideDataAudioPlayerRepository(): AudioPlayerRepository {
        return AudioPlayerRepositoryImpl()
    }

    fun provideNavigatorInteractor(): NavigatorInteractor {
        return NavigatorInteractor(provideNavigatorRepository())
    }

    private fun provideNavigatorRepository(): NavigatorRepository {
        return NavigatorRepositoryImpl(
            externalNavigator = provideExternalNavigator(),
            context = app
        )
    }

    private fun provideExternalNavigator(): ExternalNavigator {
        return ExternalNavigator(app)
    }

    fun provideSettingsInteractor(): SettingsInteractor {
        return SettingsInteractor(provideSettingsRepository())
    }

    private fun provideSettingsRepository(): SettingsRepository {
        return SettingsRepositoryImpl(
            app.getSharedPreferences(
                PLAYLISTMAKER_SHARED_PREFS,
                Context.MODE_PRIVATE
            )
        )
    }
}