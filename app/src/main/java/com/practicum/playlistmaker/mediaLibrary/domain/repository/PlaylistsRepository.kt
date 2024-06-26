package com.practicum.playlistmaker.mediaLibrary.domain.repository

import com.practicum.playlistmaker.mediaLibrary.domain.entities.Playlist
import com.practicum.playlistmaker.search.domain.entities.TrackInfo
import kotlinx.coroutines.flow.Flow

interface PlaylistsRepository {
    suspend fun insertPlaylist(playlist: Playlist)

    suspend fun deletePlaylist(playlist: Playlist)

    fun getAllPlaylists(): Flow<List<Playlist>>

    suspend fun getTracksOfPlaylist(playlistName: String): List<TrackInfo>

    suspend fun addTrackToTracklist(trackInfo: TrackInfo, playlist: Playlist): Boolean

    suspend fun getListOfNamesOfAllPlaylists(playlistName: String): List<String>

    suspend fun saveImageToAppStorage(playlistImage: String, playlistName: String)

    suspend fun getAllPlaylistDetails(playlistName: String): Pair<Playlist, List<TrackInfo>>

    suspend fun deleteTrackFromPlaylist(trackId: Int, playlistName: String)

    suspend fun updateTracksInPlaylist(playlistName: String): List<TrackInfo>

}