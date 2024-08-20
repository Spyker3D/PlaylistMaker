package com.practicum.playlistmaker.mediaLibrary.data.repository

import android.content.Context
import android.net.Uri
import android.os.Environment
import androidx.core.net.toUri
import androidx.room.withTransaction
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

    override suspend fun addTrackToTracklist(trackInfo: TrackInfo, playlist: Playlist): Boolean =
        appDatabase.withTransaction {
            val isTrackInPlaylist = appDatabase.playlistDao()
                .isTrackInPlaylist(
                    trackId = trackInfo.trackId,
                    playlistName = playlist.playlistName
                )
            if (isTrackInPlaylist) return@withTransaction false

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
            true
        }

    override suspend fun saveImageToAppStorage(playlistImage: Uri, playlistName: String): String {
//        val file = context.getFileByPlaylistName(playlistName)

//        _____________________________
//        val playlistImagesDirPath = File(
//            context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
//            "playlist_images"
//        )
//
//        if(!playlistImagesDirPath.exists()) {
//            playlistImagesDirPath.mkdirs()
//        }
//
//        fileName = playlistName
//        val filePath = File(playlistImagesDirPath, fileName)
//        __________________________

//        withContext(Dispatchers.IO) {
//            if (!file.exists()) {
//                if (playlistImage.isNotEmpty()) {
//                    context.contentResolver.openInputStream(playlistImage.toUri())!!
//                        .use { inputStream ->
//                            file.outputStream().use { outputStream ->
//                                inputStream.copyTo(outputStream)
//                            } //use - если ошибка, все равно закроет поток, если не закрывать (можно вручную ючерзе close, то останется поток)  = try with resources in Java
//                        }
//                } else {
//                    Unit
//                }
//            } else {
//                Log.e("SAVE_ALBUM_IMAGE_ERROR", "Файл с таким именем уже существует")
//            }
//        }

        return withContext(Dispatchers.IO) {
            val filePath =
                File(
                    context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                    "playlist_images"
                )
            if (!filePath.exists()) filePath.mkdirs()
            val imageName = System.currentTimeMillis().toString()

            val file = File(filePath, imageName)
            context.contentResolver.openInputStream(playlistImage)!!.use { inputStream ->
                file.outputStream().use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
            imageName
        }

    }

    override suspend fun getImagePathToAppStorage(uriName: String): Uri {
        val dirPath =
            File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "playlist_images")
        val file = File(dirPath, uriName)
        return file.toUri()
    }

    override fun getAllPlaylistDetails(playlistName: String): Flow<Pair<Playlist, List<TrackInfo>>> {
        val playListEntityWithTracks =
            appDatabase.playlistDao().getTracksOfPlaylist(playlistName).map {
                Pair(
                    it.playlistEntity.mapToDomainEntity(),
                    it.tracksList.map {
                        trackInfo -> trackInfo.mapToDomainEntity()
                    }
                )
            }
        return playListEntityWithTracks
    }

    override suspend fun deleteTrackFromPlaylist(trackId: Int, playlistName: String) {
        appDatabase.withTransaction {
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
            appDatabase.trackInPlaylistDao().deleteAllNotInPlaylist()
        }
    }

    override suspend fun deletePlaylist(playlist: Playlist) {
        appDatabase.withTransaction {
            appDatabase.playlistDao().deleteFromPlaylists(playlist.mapToDbEntity())
            appDatabase.trackInPlaylistDao().deleteAllNotInPlaylist()
        }
    }

    override suspend fun getPlaylistByName(playlistName: String): Playlist {
        return appDatabase.playlistDao().getPlaylistByName(playlistName).mapToDomainEntity()
    }

    override suspend fun updateExistingPlaylist(
        playlistName: String,
        playlistNameSecondary: String,
        playlistDescription: String?,
        numberOfTracks: Int,
        imagePath: String?,
    ) {
        appDatabase.playlistDao().updateExistingPlaylist(
            playlistName = playlistName,
            playlistNameSecondary = playlistNameSecondary,
            playlistDescription = playlistDescription,
            numberOfTracks = numberOfTracks,
            imagePath = imagePath.toString()
        )
    }

    override suspend fun isPlaylistAlreadyCreated(playlistNameSecondary: String): Boolean {
        return appDatabase.playlistDao().isPlaylistAlreadyCreated(playlistNameSecondary)
    }
}
