package com.practicum.playlistmaker.mediaLibrary.data.repository

import android.content.Context
import android.os.Environment
import android.util.Log
import androidx.core.net.toUri
import com.practicum.playlistmaker.mediaLibrary.data.converters.PlaylistDbConverter.mapToDbEntity
import com.practicum.playlistmaker.mediaLibrary.data.converters.PlaylistDbConverter.mapToDomainEntity
import com.practicum.playlistmaker.mediaLibrary.data.converters.TrackDbConverter.mapToDbTrackInPlaylistsEntity
import com.practicum.playlistmaker.mediaLibrary.data.converters.TrackDbConverter.mapToDomainEntity
import com.practicum.playlistmaker.mediaLibrary.data.db.AppDatabase
import com.practicum.playlistmaker.mediaLibrary.data.db.entity.PlaylistEntityTrackInPlaylistEntityCrossRef
import com.practicum.playlistmaker.mediaLibrary.domain.entities.Playlist
import com.practicum.playlistmaker.mediaLibrary.domain.repository.PlaylistsRepository
import com.practicum.playlistmaker.player.presentation.TrackInPlaylistState
import com.practicum.playlistmaker.search.domain.entities.TrackInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.io.File

fun Context.getFileByPlaylistName(playlistName: String) = File(
    getExternalFilesDir(Environment.DIRECTORY_PICTURES),
    playlistName
)

class PlaylistsRepositoryImpl(private val context: Context, private val appDatabase: AppDatabase) :
    PlaylistsRepository {
    override suspend fun insertPlaylist(playlist: Playlist) {
        appDatabase.playlistDao().insertPlaylist(playlist.mapToDbEntity())
    }

    override fun getAllPlaylists(): Flow<List<Playlist>> {
        val playlistsList = appDatabase.playlistDao().getAllPlaylists()
        return playlistsList.map { list -> list.map { it.mapToDomainEntity() } }
    }

    override suspend fun getTracksOfPlaylist(playlistName: String): List<TrackInfo> {
        return appDatabase.playlistDao().getTracksOfPlaylist(playlistName).tracksList.map {
            it.mapToDomainEntity()
        }
    }

    override suspend fun addTrackToTracklist(trackInfo: TrackInfo, playlist: Playlist): Boolean {

        val listOfTrackIds = getTracksOfPlaylist(playlistName = playlist.playlistName)
            .map { it.trackId }
        if (trackInfo.trackId !in listOfTrackIds) {
            val timeAdded = System.currentTimeMillis()
            appDatabase.trackInPlaylistDao()
                .insertToTracksInPlaylists(trackInfo.mapToDbTrackInPlaylistsEntity(timeAdded))

            val currentNumberOfTracks =
            appDatabase.playlistDao().getNumberOfTracksInPlaylist(playlist.playlistName)

        appDatabase.playlistDao()
            .updateNumberOfTracksInPlaylist(playlist.playlistName, currentNumberOfTracks + 1)

            appDatabase.playlistDao().insertPlaylistTrackCrossRef(
                PlaylistEntityTrackInPlaylistEntityCrossRef(
                    playlist.playlistName,
                    trackInfo.trackId
                )
            )
            return true
        } else {
            return false
        }
    }

    override suspend fun getListOfNamesOfAllPlaylists(playlistName: String): List<String> {
        return appDatabase.playlistDao().getListOfNamesOfAllPlaylists(playlistName)
    }

    override suspend fun saveImageToAppStorage(playlistImage: String, playlistName: String) {
        val file = context.getFileByPlaylistName(playlistName)

        withContext(Dispatchers.IO) {
            if (!file.exists()) {
                if (playlistImage.isNotEmpty()) {
                    context.contentResolver.openInputStream(playlistImage.toUri())!!
                        .use { inputStream ->
                            file.outputStream().use { outputStream ->
                                inputStream.copyTo(outputStream)
                            } //use - если ошибка, все равно закроет поток, если не закрывать (можно вручную ючерзе close, то останется поток)  = try with resources in Java
                        }
                } else {
                    Unit
                }
            } else {
                Log.e("SAVE_ALBUM_IMAGE_ERROR", "Файл с таким именем уже существует")
            }
        }
    }

    override suspend fun getAllPlaylistDetails(playlistName: String): Pair<Playlist, List<TrackInfo>> {
        val playListEntityWithTracks = appDatabase.playlistDao().getTracksOfPlaylist(playlistName)
        return Pair(
            playListEntityWithTracks.playlistEntity.mapToDomainEntity(),
            playListEntityWithTracks.tracksList.map {
                it.mapToDomainEntity()
            })
    }

    override suspend fun deleteTrackFromPlaylist(trackId: Int, playlistName: String) {
        val currentNumberOfTracks =
            appDatabase.playlistDao().getNumberOfTracksInPlaylist(playlistName)

        appDatabase.playlistDao()
            .updateNumberOfTracksInPlaylist(playlistName, currentNumberOfTracks - 1)

        appDatabase.playlistDao().deletePlaylistTrackCrossRef(
            PlaylistEntityTrackInPlaylistEntityCrossRef(
                trackId = trackId,
                playlistName = playlistName
            )
        )
        val trackIdsOfAllPlaylists =
            appDatabase.playlistDao().getAllCrossRefEntries().map { it.trackId }
        if (trackId !in trackIdsOfAllPlaylists) {
            appDatabase.trackInPlaylistDao().deleteFromTracksInPlaylists(trackId)
        }
    }

    override suspend fun updateTracksInPlaylist(playlistName: String): List<TrackInfo> {
        val playListEntityWithTracks = appDatabase.playlistDao().getTracksOfPlaylist(playlistName)
        return playListEntityWithTracks.tracksList.map { it.mapToDomainEntity() }
    }

    override suspend fun deletePlaylist(playlist: Playlist) {

        appDatabase.playlistDao().deleteFromPlaylists(playlist.mapToDbEntity())
        appDatabase.playlistDao().deleteCrossRefBasedOnPlaylist(playlist.playlistName)
        val allTracksIds = appDatabase.trackInPlaylistDao().getAllTracksIdsInPlaylists()
        val crossRefTracksIds = appDatabase.playlistDao().getAllCrossRefEntries().map {
            it.trackId
        }
        allTracksIds.forEach {
            if (it !in crossRefTracksIds) {
                appDatabase.trackInPlaylistDao().deleteFromTracksInPlaylists(it)
            }
        }
    }
}
