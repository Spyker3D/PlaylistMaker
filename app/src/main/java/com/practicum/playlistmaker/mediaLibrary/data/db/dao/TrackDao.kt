package com.practicum.playlistmaker.mediaLibrary.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.practicum.playlistmaker.mediaLibrary.data.db.entity.TrackEntity

@Dao
interface TrackDao {

    @Insert(entity = TrackEntity::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertToFavouriteTracks(track: TrackEntity)

    @Delete(entity = TrackEntity::class)
    suspend fun deleteFromFavouriteTracks(track: TrackEntity)

    @Query("SELECT * FROM favorite_tracks_table ORDER BY time_added DESC")
    suspend fun getFavouriteTracks(): List<TrackEntity>

    @Query("SELECT remote_track_id FROM favorite_tracks_table")
    suspend fun getFavouriteTracksIds(): List<Int>
}