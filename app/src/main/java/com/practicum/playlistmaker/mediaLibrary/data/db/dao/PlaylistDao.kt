package com.practicum.playlistmaker.mediaLibrary.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.practicum.playlistmaker.mediaLibrary.data.db.entity.PlaylistEntity
import com.practicum.playlistmaker.mediaLibrary.data.db.entity.PlaylistEntityTrackInPlaylistEntityCrossRef
import com.practicum.playlistmaker.mediaLibrary.data.db.entity.relations.PlaylistEntityWithTracks
import com.practicum.playlistmaker.mediaLibrary.data.db.entity.relations.TrackEntityWithPlaylists
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistDao {
    @Insert(entity = PlaylistEntity::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylist(playlist: PlaylistEntity)

    @Insert(
        entity = PlaylistEntityTrackInPlaylistEntityCrossRef::class,
        onConflict = OnConflictStrategy.ABORT
    )
    suspend fun insertPlaylistTrackCrossRef(crossRef: PlaylistEntityTrackInPlaylistEntityCrossRef)

    @Delete(entity = PlaylistEntity::class)
    suspend fun deleteFromPlaylists(playlist: PlaylistEntity)

    @Delete(entity = PlaylistEntityTrackInPlaylistEntityCrossRef::class)
    suspend fun deletePlaylistTrackCrossRef(crossRef: PlaylistEntityTrackInPlaylistEntityCrossRef)

    @Query("SELECT * FROM playlist_track_cross_ref")
    suspend fun getAllCrossRefEntries(): List<PlaylistEntityTrackInPlaylistEntityCrossRef>

    @Query("SELECT * FROM playlists_table ORDER BY playlist_name_detailed DESC")
    fun getAllPlaylists(): Flow<List<PlaylistEntity>>

    @Query("SELECT :playlistName IN (SELECT playlist_name_detailed FROM playlists_table)")
    suspend fun getListOfNamesOfSelectedPlaylist(playlistName: String): List<String>

    @Transaction
    @Query("SELECT * FROM playlists_table WHERE playlist_name = :playlistName")
    fun getTracksOfPlaylist(playlistName: String): Flow<PlaylistEntityWithTracks>

    @Transaction
    @Query("SELECT * FROM tracks_in_playlists_table WHERE remote_track_id = :trackId")
    suspend fun getPlaylistsOfTrack(trackId: Int): TrackEntityWithPlaylists

    @Query("UPDATE playlists_table SET number_of_tracks = :numberOfTracks WHERE playlist_name = :playlistName")
    suspend fun updateNumberOfTracksInPlaylist(playlistName: String, numberOfTracks: Int)

    @Query("SELECT number_of_tracks FROM playlists_table WHERE playlist_name = :playlistName")
    suspend fun getNumberOfTracksInPlaylist(playlistName: String): Int

    @Query("SELECT playlist_name FROM playlists_table WHERE playlist_name_detailed = :playlistNameSecondary")
    suspend fun getPrimaryKeyBySecondaryPlaylistName(playlistNameSecondary: String): String

    @Query(
        "SELECT :trackId IN (SELECT remote_track_id FROM playlist_track_cross_ref" +
                " WHERE playlist_name = :playlistName)"
    )
    suspend fun isTrackInPlaylist(trackId: Int, playlistName: String): Boolean

    @Query("SELECT :playlistNameSecondary IN (SELECT playlist_name_detailed FROM playlists_table)")
    suspend fun isPlaylistAlreadyCreated(playlistNameSecondary: String): Boolean

    @Query("SELECT * FROM playlists_table WHERE playlist_name = :playlistName")
    suspend fun getPlaylistByName(playlistName: String): PlaylistEntity

    @Query(
        "UPDATE playlists_table SET playlist_name_detailed = :playlistNameSecondary, " +
                "description = :playlistDescription," +
                " number_of_tracks = :numberOfTracks, path_to_image = :imagePath " +
                "WHERE playlist_name =:playlistName"
    )
    suspend fun updateExistingPlaylist(
        playlistName: String,
        playlistNameSecondary: String,
        playlistDescription: String?,
        numberOfTracks: Int,
        imagePath: String?,
    )


}