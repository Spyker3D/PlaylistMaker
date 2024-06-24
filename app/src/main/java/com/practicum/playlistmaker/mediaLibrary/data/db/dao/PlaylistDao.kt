package com.practicum.playlistmaker.mediaLibrary.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.practicum.playlistmaker.mediaLibrary.data.db.entity.PlaylistEntity
import com.practicum.playlistmaker.mediaLibrary.data.db.entity.PlaylistEntityTrackInPlaylistEntityCrossRef
import com.practicum.playlistmaker.mediaLibrary.data.db.entity.relations.PlaylistEntityWithTracks
import com.practicum.playlistmaker.mediaLibrary.data.db.entity.relations.TrackEntityWithPlaylists

@Dao
interface PlaylistDao {
    @Insert(entity = PlaylistEntity::class, onConflict = OnConflictStrategy.ABORT)
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

    @Query("SELECT * FROM playlists_table ORDER BY playlist_name DESC")
    suspend fun getAllPlaylists(): List<PlaylistEntity>

    @Query("SELECT playlist_name FROM playlists_table WHERE playlist_name = :playlistName")
    suspend fun getListOfNamesOfAllPlaylists(playlistName: String): List<String>

    @Transaction
    @Query("SELECT * FROM playlists_table WHERE playlist_name = :playlistName")
    suspend fun getTracksOfPlaylist(playlistName: String): PlaylistEntityWithTracks

    @Transaction
    @Query("SELECT * FROM tracks_in_playlists_table WHERE remote_track_id = :trackId")
    suspend fun getPlaylistsOfTrack(trackId: Int): TrackEntityWithPlaylists

    @Query("UPDATE playlists_table SET number_of_tracks = number_of_tracks + 1 WHERE playlist_name = :playlistName")
    suspend fun updateNumberOfTracksInPlaylist(playlistName: String)

    @Query("SELECT number_of_tracks FROM playlists_table WHERE playlist_name =:playlistName")
    suspend fun getNumberOfTracksInPlaylist(playlistName: String): Int
}