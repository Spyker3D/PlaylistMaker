package com.practicum.playlistmaker.mediaLibrary.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.practicum.playlistmaker.mediaLibrary.data.db.entity.FavouriteTrackEntity

@Dao
interface FavouriteTrackDao {

    @Insert(entity = FavouriteTrackEntity::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertToFavouriteTracks(track: FavouriteTrackEntity)

    @Delete(entity = FavouriteTrackEntity::class)
    suspend fun deleteFromFavouriteTracks(track: FavouriteTrackEntity)

    @Query("SELECT * FROM favorite_tracks_table ORDER BY time_added DESC")
    suspend fun getFavouriteTracks(): List<FavouriteTrackEntity>

    @Query("SELECT remote_track_id FROM favorite_tracks_table")
    suspend fun getFavouriteTracksIds(): List<Int>

}