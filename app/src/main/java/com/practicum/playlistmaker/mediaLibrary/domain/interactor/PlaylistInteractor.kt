package com.practicum.playlistmaker.mediaLibrary.domain.interactor

import android.net.Uri
import com.practicum.playlistmaker.mediaLibrary.domain.entities.Playlist
import com.practicum.playlistmaker.mediaLibrary.domain.repository.PlaylistsRepository
import com.practicum.playlistmaker.search.domain.entities.TrackInfo
import com.practicum.playlistmaker.search.presentation.entities.Track
import com.practicum.playlistmaker.search.presentation.mapper.TrackPresentationMapper.mapToDomain
import kotlinx.coroutines.flow.Flow

class PlaylistInteractor(private val playlistsRepository: PlaylistsRepository) {
    suspend fun insertPlaylist(playlist: Playlist) {
        playlistsRepository.insertPlaylist(playlist)
    }

    suspend fun deletePlaylist(playlist: Playlist) {
        playlistsRepository.deletePlaylist(playlist)
    }

    fun getAllPlaylists(): Flow<List<Playlist>> = playlistsRepository.getAllPlaylists()

//    suspend fun getTracksOfPlaylist(playlistName: String): List<TrackInfo> {
//        return playlistsRepository.getTracksOfPlaylist(playlistName)
//    }

    suspend fun addTrackToPlaylist(track: Track, playlist: Playlist): Boolean {
        return playlistsRepository.addTrackToTracklist(track.mapToDomain(), playlist)
    }

//    suspend fun getListOfNamesOfAllPlaylists(playlistName: String): List<String> {
//        return playlistsRepository.getListOfNamesOfAllPlaylists(playlistName)
//    }

    suspend fun saveImageToAppStorage(playlistImage: Uri, playlistName: String): String {
        return playlistsRepository.saveImageToAppStorage(
            playlistImage = playlistImage,
            playlistName = playlistName
        )
    }

    fun getAllPlaylistDetails(playlistName: String): Flow<Pair<Playlist, List<TrackInfo>>> =
        playlistsRepository.getAllPlaylistDetails(playlistName)

    suspend fun deleteTrackFromPlaylist(trackId: Int, playlistName: String) {
        playlistsRepository.deleteTrackFromPlaylist(trackId, playlistName)
    }

//    suspend fun updateTracksInPlaylist(playlistName: String): List<TrackInfo> {
//        return playlistsRepository.updateTracksInPlaylist(playlistName)
//    }

    suspend fun getPlaylistByName(playlistName: String): Playlist {
        return playlistsRepository.getPlaylistByName(playlistName)
    }

    suspend fun getImagePathToAppStorage(uriName: String): Uri {
        return playlistsRepository.getImagePathToAppStorage(uriName)
    }

    suspend fun updateExistingPlaylist(
        playlistName: String,
        playlistNameSecondary: String,
        playlistDescription: String?,
        numberOfTracks: Int,
        imagePath: String?,
    ) {
        playlistsRepository.updateExistingPlaylist(
            playlistName = playlistName,
            playlistNameSecondary = playlistNameSecondary,
            playlistDescription = playlistDescription,
            numberOfTracks = numberOfTracks,
            imagePath = imagePath
        )
    }

    suspend fun isPlaylistAlreadyCreated(playlistNameSecondary: String): Boolean {
        return playlistsRepository.isPlaylistAlreadyCreated(playlistNameSecondary)
    }

}