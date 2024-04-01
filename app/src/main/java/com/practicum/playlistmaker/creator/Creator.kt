package com.practicum.playlistmaker.creator

import android.content.Context
import com.practicum.playlistmaker.data.network.NetworkClient
import com.practicum.playlistmaker.data.network.RetrofitNetworkClient
import com.practicum.playlistmaker.data.repository.AudioPlayerRepositoryImpl
import com.practicum.playlistmaker.data.repository.TrackSearchRepositoryImpl
import com.practicum.playlistmaker.data.repository.TrackStorageRepositoryImpl
import com.practicum.playlistmaker.data.storage.SharedPrefsStorage
import com.practicum.playlistmaker.data.storage.TrackStorage
import com.practicum.playlistmaker.domain.repository.AudioPlayerRepository
import com.practicum.playlistmaker.domain.interactor.AudioPlayerInteractor
import com.practicum.playlistmaker.domain.interactor.GetHistoryTrackUseCase
import com.practicum.playlistmaker.domain.interactor.SaveHistoryTrackUseCase
import com.practicum.playlistmaker.domain.interactor.SearchTrackUseCase
import com.practicum.playlistmaker.domain.repository.TrackSearchRepository
import com.practicum.playlistmaker.domain.repository.TrackStorageRepository

class Creator(private val context: Context) {

    fun provideSearchTrackUseCase(): SearchTrackUseCase {
        return SearchTrackUseCase(provideTrackSearchRepository())
    }

    private fun provideTrackSearchRepository(): TrackSearchRepository {
        return TrackSearchRepositoryImpl(provideNetworkClient())
    }

    private fun provideNetworkClient(): NetworkClient {
        return RetrofitNetworkClient()
    }

    fun provideSaveHistoryTrackUseCase(): SaveHistoryTrackUseCase {
        return SaveHistoryTrackUseCase(provideStorageRepository())
    }

    private fun provideStorageRepository(): TrackStorageRepository {
        return TrackStorageRepositoryImpl(provideTrackStorage())
    }

    private fun provideTrackStorage(): TrackStorage {
        return SharedPrefsStorage(context)
    }

    fun provideGetHistoryTrackUseCase(): GetHistoryTrackUseCase {
        return GetHistoryTrackUseCase(provideStorageRepository())
    }

    fun provideAudioPlayerInteractor(): AudioPlayerInteractor {
        return AudioPlayerInteractor(provideDataAudioPlayerRepository())
    }

    private fun provideDataAudioPlayerRepository(): AudioPlayerRepository {
        return AudioPlayerRepositoryImpl()
    }
}