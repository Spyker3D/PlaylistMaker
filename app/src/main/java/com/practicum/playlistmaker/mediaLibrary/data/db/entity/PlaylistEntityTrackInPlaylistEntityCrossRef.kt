package com.practicum.playlistmaker.mediaLibrary.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(tableName = "playlist_track_cross_ref", primaryKeys = ["playlist_name", "remote_track_id"])
data class PlaylistEntityTrackInPlaylistEntityCrossRef(
    @ColumnInfo(name = "playlist_name")
    val playlistName: String,
    @ColumnInfo(name = "remote_track_id")
    val trackId: Int,
)