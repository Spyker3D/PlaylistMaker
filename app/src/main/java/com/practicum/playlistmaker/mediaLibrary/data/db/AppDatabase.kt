package com.practicum.playlistmaker.mediaLibrary.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.practicum.playlistmaker.mediaLibrary.data.db.dao.PlaylistDao
import com.practicum.playlistmaker.mediaLibrary.data.db.dao.TrackDao
import com.practicum.playlistmaker.mediaLibrary.data.db.dao.TrackInPlaylistDao
import com.practicum.playlistmaker.mediaLibrary.data.db.entity.PlaylistEntity
import com.practicum.playlistmaker.mediaLibrary.data.db.entity.PlaylistEntityTrackInPlaylistEntityCrossRef
import com.practicum.playlistmaker.mediaLibrary.data.db.entity.TrackEntity
import com.practicum.playlistmaker.mediaLibrary.data.db.entity.TrackInPlaylistEntity

@Database(
    entities = [
        TrackEntity::class,
        TrackInPlaylistEntity::class,
        PlaylistEntity::class,
        PlaylistEntityTrackInPlaylistEntityCrossRef::class
               ],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun trackDao(): TrackDao

    abstract fun playlistDao(): PlaylistDao

    abstract fun trackInPlaylistDao(): TrackInPlaylistDao

    companion object {
        @Volatile
        private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            synchronized(this) {
                return instance ?: Room.databaseBuilder(
                    context,
                    AppDatabase::class.java,
                    "database.db"
                ).build().also {
                    instance = it
                }
            }
        }
    }
}