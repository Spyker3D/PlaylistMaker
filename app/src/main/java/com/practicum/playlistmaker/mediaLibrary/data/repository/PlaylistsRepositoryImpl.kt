package com.practicum.playlistmaker.mediaLibrary.data.repository

import android.content.Context
import android.os.Environment
import android.util.Log
import androidx.core.net.toUri
import androidx.lifecycle.lifecycleScope
import com.practicum.playlistmaker.mediaLibrary.data.converters.PlaylistDbConverter.mapToDbEntity
import com.practicum.playlistmaker.mediaLibrary.data.converters.PlaylistDbConverter.mapToDomainEntity
import com.practicum.playlistmaker.mediaLibrary.data.converters.TrackDbConverter.mapToDbTrackInPlaylistsEntity
import com.practicum.playlistmaker.mediaLibrary.data.converters.TrackDbConverter.mapToDomainEntity
import com.practicum.playlistmaker.mediaLibrary.data.db.AppDatabase
import com.practicum.playlistmaker.mediaLibrary.data.db.entity.PlaylistEntityTrackInPlaylistEntityCrossRef
import com.practicum.playlistmaker.mediaLibrary.domain.entities.Playlist
import com.practicum.playlistmaker.mediaLibrary.domain.repository.PlaylistsRepository
import com.practicum.playlistmaker.search.domain.entities.TrackInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
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

    override suspend fun deletePlaylist(playlist: Playlist) {
        appDatabase.playlistDao().deleteFromPlaylists(playlist.mapToDbEntity())
    }

    override fun getAllPlaylists(): Flow<List<Playlist>> = flow {
        val playlistsList = appDatabase.playlistDao().getAllPlaylists()
        emit(playlistsList.map { it.mapToDomainEntity() })
    }

    override suspend fun getTracksOfPlaylist(playlistName: String): List<TrackInfo> {
        return appDatabase.playlistDao().getTracksOfPlaylist(playlistName).tracksIds.map {
            it.mapToDomainEntity()
        }
    }

    override suspend fun addTrackToTracklist(trackInfo: TrackInfo, playlist: Playlist) {
        val timeAdded = System.currentTimeMillis()
        appDatabase.trackInPlaylistDao()
            .insertToTracksInPlaylists(trackInfo.mapToDbTrackInPlaylistsEntity(timeAdded))

        appDatabase.playlistDao().updateNumberOfTracksInPlaylist(playlist.playlistName)

        appDatabase.playlistDao().insertPlaylistTrackCrossRef(
            PlaylistEntityTrackInPlaylistEntityCrossRef(
                playlist.playlistName,
                trackInfo.trackId
            )
        )
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
}
