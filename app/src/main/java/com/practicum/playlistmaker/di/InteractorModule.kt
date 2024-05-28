package com.practicum.playlistmaker.di

import com.practicum.playlistmaker.player.domain.interactor.AudioPlayerInteractor
import com.practicum.playlistmaker.search.domain.interactor.GetHistoryTrackUseCase
import com.practicum.playlistmaker.search.domain.interactor.SaveHistoryTrackUseCase
import com.practicum.playlistmaker.search.domain.interactor.SearchTrackUseCase
import com.practicum.playlistmaker.settings.domain.interactor.SettingsInteractor
import com.practicum.playlistmaker.sharing.interactor.NavigatorInteractor
import org.koin.dsl.module

val interactorModule = module {
    factory {
        AudioPlayerInteractor(audioPlayerRepository = get())
    }

    factory {
        SearchTrackUseCase(trackSearchRepository = get())
    }

    factory {
        SaveHistoryTrackUseCase(trackStorage = get())
    }

    factory {
        GetHistoryTrackUseCase(trackStorage = get())
    }

    factory {
        NavigatorInteractor(navigatorRepository = get())
    }

    factory {
        SettingsInteractor(settingsRepository = get())
    }
}
