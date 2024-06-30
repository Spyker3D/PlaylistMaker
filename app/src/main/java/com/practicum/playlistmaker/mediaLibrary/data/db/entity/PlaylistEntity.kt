package com.practicum.playlistmaker.mediaLibrary.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "playlists_table")
data class PlaylistEntity(
//    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "playlist_id")
//    val playlistId: Int,
    @PrimaryKey(autoGenerate = false) @ColumnInfo(name = "playlist_name")
    val playlistName: String,
    @ColumnInfo(name = "playlist_name_detailed")
    val playlistNameSecondary: String,
    @ColumnInfo(name = "description")
    val playlistDescription: String?,
    @ColumnInfo(name = "path_to_image")
    val pathToImage: String?,
    @ColumnInfo(name = "number_of_tracks")
    val numberOfTracks: Int = 0,
)