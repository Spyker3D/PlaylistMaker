package com.practicum.playlistmaker.mediaLibrary.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName = "playlist_track_cross_ref",
    primaryKeys = ["playlist_name", "remote_track_id"],
    foreignKeys = [
        ForeignKey(
            entity = PlaylistEntity::class,
            parentColumns = ["playlist_name"],
            childColumns = ["playlist_name"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = TrackInPlaylistEntity::class,
            parentColumns = ["remote_track_id"],
            childColumns = ["remote_track_id"],
            onDelete = ForeignKey.RESTRICT
        )
    ]
)
data class PlaylistEntityTrackInPlaylistEntityCrossRef(
    @ColumnInfo(name = "playlist_name")
    val playlistName: String,
    @ColumnInfo(name = "remote_track_id")
    val trackId: Int,
)