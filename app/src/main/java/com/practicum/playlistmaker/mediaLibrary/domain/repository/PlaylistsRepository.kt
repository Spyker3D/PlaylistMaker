package com.practicum.playlistmaker.mediaLibrary.domain.repository

import android.net.Uri
import com.practicum.playlistmaker.mediaLibrary.domain.entities.Playlist
import com.practicum.playlistmaker.search.domain.entities.TrackInfo
import kotlinx.coroutines.flow.Flow

interface PlaylistsRepository {
    suspend fun insertPlaylist(playlist: Playlist)

    suspend fun deletePlaylist(playlist: Playlist)

    fun getAllPlaylists(): Flow<List<Playlist>>

    suspend fun addTrackToTracklist(trackInfo: TrackInfo, playlist: Playlist): Boolean

    suspend fun saveImageToAppStorage(playlistImage: Uri, playlistName: String): String

    fun getAllPlaylistDetails(playlistName: String): Flow<Pair<Playlist, List<TrackInfo>>>

    suspend fun deleteTrackFromPlaylist(trackId: Int, playlistName: String)

    suspend fun getPlaylistByName(playlistName: String): Playlist

    suspend fun getImagePathToAppStorage(uriName: String): Uri

    suspend fun updateExistingPlaylist(
        playlistName: String,
        playlistNameSecondary: String,
        playlistDescription: String?,
        numberOfTracks: Int,
        imagePath: String?,
    )

    suspend fun isPlaylistAlreadyCreated(playlistNameSecondary: String): Boolean

}