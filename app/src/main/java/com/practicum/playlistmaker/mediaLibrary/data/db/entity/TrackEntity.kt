package com.practicum.playlistmaker.mediaLibrary.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_tracks_table")
data class TrackEntity(
    @PrimaryKey @ColumnInfo(name = "remote_track_id")
    val trackId: Int,
    @ColumnInfo(name = "track_name")
    val trackName: String,
    @ColumnInfo(name = "track_url")
    val trackUrl: String,
    @ColumnInfo(name = "cover_url")
    val coverUrl: String?,
    @ColumnInfo(name = "cover_url_large")
    val coverUrlLarge: String?,
    @ColumnInfo(name = "artist_name")
    val artistName: String,
    @ColumnInfo(name = "album_name")
    val collectionName: String?,
    @ColumnInfo(name = "release_year")
    val releaseYear: String?,
    @ColumnInfo(name = "genre_name")
    val genreName: String?,
    @ColumnInfo(name = "country")
    val country: String?,
    @ColumnInfo(name = "track_length")
    val trackTimeMillis: Long,
    @ColumnInfo(name = "time_added")
    val timeAdded: Long,
)
