package com.practicum.playlistmaker.di

import com.practicum.playlistmaker.player.domain.interactor.AudioPlayerInteractor
import com.practicum.playlistmaker.search.domain.interactor.GetHistoryTrackUseCase
import com.practicum.playlistmaker.search.domain.interactor.SaveHistoryTrackUseCase
import com.practicum.playlistmaker.search.domain.interactor.SelectedTrackUseCase
import com.practicum.playlistmaker.search.domain.interactor.SearchTrackUseCase
import com.practicum.playlistmaker.settings.domain.interactor.SettingsInteractor
import com.practicum.playlistmaker.sharing.interactor.NavigatorInteractor
import org.koin.dsl.module

val interactorModule = module {
    factory {
        AudioPlayerInteractor(audioPlayerRepository = get())
    }

    single {
        SearchTrackUseCase(trackSearchRepository = get())
    }

    single {
        SaveHistoryTrackUseCase(trackStorage = get())
    }

    single {
        GetHistoryTrackUseCase(trackStorage = get())
    }

    single {
        SelectedTrackUseCase(trackStorage = get())
    }

    single {
        NavigatorInteractor(navigatorRepository = get())
    }

    single {
        SettingsInteractor(settingsRepository = get())
    }
}
