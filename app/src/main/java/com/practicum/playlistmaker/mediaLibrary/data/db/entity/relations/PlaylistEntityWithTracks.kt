package com.practicum.playlistmaker.mediaLibrary.data.db.entity.relations

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.practicum.playlistmaker.mediaLibrary.data.db.entity.PlaylistEntity
import com.practicum.playlistmaker.mediaLibrary.data.db.entity.PlaylistEntityTrackInPlaylistEntityCrossRef
import com.practicum.playlistmaker.mediaLibrary.data.db.entity.TrackEntity
import com.practicum.playlistmaker.mediaLibrary.data.db.entity.TrackInPlaylistEntity

data class PlaylistEntityWithTracks(
    @Embedded val playlistEntity: PlaylistEntity,
    @Relation(
        parentColumn = "playlist_name",
        entityColumn = "remote_track_id",
        associateBy = Junction(PlaylistEntityTrackInPlaylistEntityCrossRef::class)
    )
    val tracksIds: List<TrackInPlaylistEntity>
)