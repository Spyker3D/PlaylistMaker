package com.practicum.playlistmaker.di

import androidx.room.util.appendPlaceholders
import com.practicum.playlistmaker.mediaLibrary.data.repository.FavouriteTracksRepositoryImpl
import com.practicum.playlistmaker.mediaLibrary.domain.repository.FavouriteTracksRepository
import com.practicum.playlistmaker.player.data.repository.AudioPlayerRepositoryImpl
import com.practicum.playlistmaker.player.domain.repository.AudioPlayerRepository
import com.practicum.playlistmaker.search.data.repository.TrackSearchRepositoryImpl
import com.practicum.playlistmaker.search.data.repository.TrackStorageRepositoryImpl
import com.practicum.playlistmaker.search.domain.repository.TrackSearchRepository
import com.practicum.playlistmaker.search.domain.repository.TrackStorageRepository
import com.practicum.playlistmaker.settings.data.repository.SettingsRepositoryImpl
import com.practicum.playlistmaker.settings.domain.repository.SettingsRepository
import com.practicum.playlistmaker.sharing.data.repository.NavigatorRepositoryImpl
import com.practicum.playlistmaker.sharing.domain.externalNavigator.NavigatorRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val repositoryModule = module {
    factory <AudioPlayerRepository> {
        AudioPlayerRepositoryImpl()
    }

    single<TrackSearchRepository> {
        TrackSearchRepositoryImpl(networkClient = get(), appDatabase = get())
    }

    single<TrackStorageRepository> {
        TrackStorageRepositoryImpl(trackStorage = get())
    }

    single<NavigatorRepository> {
        NavigatorRepositoryImpl(externalNavigator = get(), context = androidContext())
    }

    single<SettingsRepository> {
        SettingsRepositoryImpl(get())
    }

    single<FavouriteTracksRepository> {
        FavouriteTracksRepositoryImpl(appDatabase = get())
    }
}



