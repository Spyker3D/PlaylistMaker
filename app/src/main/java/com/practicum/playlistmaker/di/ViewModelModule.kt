package com.practicum.playlistmaker.di

import com.practicum.playlistmaker.main.presentation.MainViewModel
import com.practicum.playlistmaker.mediaLibrary.presentation.MediaLibraryViewModel
import com.practicum.playlistmaker.mediaLibrary.presentation.editplaylist.EditPlaylistViewModel
import com.practicum.playlistmaker.mediaLibrary.presentation.favoritetracks.FavoriteTracksViewModel
import com.practicum.playlistmaker.mediaLibrary.presentation.newplaylist.NewPlaylistViewModel
import com.practicum.playlistmaker.mediaLibrary.presentation.playlistdetailsandedit.PlaylistDetailsViewModel
import com.practicum.playlistmaker.mediaLibrary.presentation.playlists.PlaylistsViewModel
import com.practicum.playlistmaker.player.presentation.PlayerViewModel
import com.practicum.playlistmaker.search.presentation.viewmodel.TrackSearchViewModel
import com.practicum.playlistmaker.settings.presentation.SettingsViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module

val viewModelModule = module {
    viewModel {
        PlayerViewModel(
            savedStateHandle = get(),
            application = androidApplication(),
            audioPlayerInteractor = get(),
            favouriteTracksInteractor = get(),
            playlistsInteractor = get()
        )
    }

    viewModel {
        TrackSearchViewModel(
            application = androidApplication(),
            searchTrackUseCase = get(),
            saveHistoryTrackUseCase = get(),
            getHistoryTrackUseCase = get(),
        )
    }

    viewModel {
        SettingsViewModel(
            application = androidApplication(),
            navigatorInteractor = get(),
            settingsInteractor = get()
        )
    }

    viewModel {
        MainViewModel()
    }

    viewModel {
        MediaLibraryViewModel()
    }

    viewModel {
        FavoriteTracksViewModel(favouriteTracksInteractor = get())
    }

    viewModel {
        PlaylistsViewModel(playlistInteractor = get())
    }

    viewModel {
        NewPlaylistViewModel(playlistInteractor = get(), application = androidApplication())
    }

    viewModel { (playlistName: String) ->
        PlaylistDetailsViewModel(
            playlistName = get { parametersOf(playlistName) },
            playlistInteractor = get(), navigatorInteractor = get()
        )
    }

    viewModel<EditPlaylistViewModel> { (playlistId: String) ->
        EditPlaylistViewModel(
            playlistName = get { parametersOf(playlistId) },
            playlistInteractor = get(),
            application = androidApplication()
        )
    }
}

