package com.practicum.playlistmaker.mediaLibrary.domain.interactor

import com.practicum.playlistmaker.mediaLibrary.data.converters.PlaylistDbConverter.mapToDbEntity
import com.practicum.playlistmaker.mediaLibrary.data.converters.PlaylistDbConverter.mapToDomainEntity
import com.practicum.playlistmaker.mediaLibrary.data.repository.FavouriteTracksRepositoryImpl
import com.practicum.playlistmaker.mediaLibrary.domain.entities.Playlist
import com.practicum.playlistmaker.mediaLibrary.domain.repository.FavouriteTracksRepository
import com.practicum.playlistmaker.mediaLibrary.domain.repository.PlaylistsRepository
import com.practicum.playlistmaker.search.domain.entities.TrackInfo
import com.practicum.playlistmaker.search.presentation.entities.Track
import com.practicum.playlistmaker.search.presentation.mapper.TrackPresentationMapper.mapToDomain
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class PlaylistInteractor(private val playlistsRepository: PlaylistsRepository) {
    suspend fun insertPlaylist(playlist: Playlist) {
        playlistsRepository.insertPlaylist(playlist)
    }

    suspend fun deletePlaylist(playlist: Playlist) {
        playlistsRepository.deletePlaylist(playlist)
    }

    fun getAllPlaylists(): Flow<List<Playlist>> = playlistsRepository.getAllPlaylists()

    suspend fun getTracksOfPlaylist(playlistName: String): List<TrackInfo> {
        return playlistsRepository.getTracksOfPlaylist(playlistName)
    }

    suspend fun addTrackToPlaylist(track: Track, playlist: Playlist) {
        playlistsRepository.addTrackToTracklist(track.mapToDomain(), playlist)
    }

    suspend fun getListOfNamesOfAllPlaylists(playlistName: String): List<String> {
        return playlistsRepository.getListOfNamesOfAllPlaylists(playlistName)
    }

    suspend fun saveImageToAppStorage(playlistImage: String, playlistName: String) {
        playlistsRepository.saveImageToAppStorage(
            playlistImage = playlistImage,
            playlistName = playlistName
        )
    }

}