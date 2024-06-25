package com.practicum.playlistmaker.mediaLibrary.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.practicum.playlistmaker.mediaLibrary.data.db.entity.TrackInPlaylistEntity

@Dao
interface TrackInPlaylistDao {
    @Insert(entity = TrackInPlaylistEntity::class, onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertToTracksInPlaylists(track: TrackInPlaylistEntity)

    @Delete(entity = TrackInPlaylistEntity::class)
    suspend fun deleteEntity(track: TrackInPlaylistEntity)

    @Query("DELETE FROM tracks_in_playlists_table WHERE remote_track_id = :trackId" )
    suspend fun deleteFromTracksInPlaylists(trackId: Int)

    @Query("SELECT * FROM tracks_in_playlists_table ORDER BY time_added DESC")
    suspend fun getAllTracksInPlaylists(): List<TrackInPlaylistEntity>

    @Query("SELECT remote_track_id FROM tracks_in_playlists_table")
    suspend fun getAllTracksIdsInPlaylists(): List<Int>
}