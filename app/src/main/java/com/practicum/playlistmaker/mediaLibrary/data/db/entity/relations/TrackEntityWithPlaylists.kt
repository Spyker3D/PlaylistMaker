package com.practicum.playlistmaker.mediaLibrary.data.db.entity.relations

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.practicum.playlistmaker.mediaLibrary.data.db.entity.PlaylistEntity
import com.practicum.playlistmaker.mediaLibrary.data.db.entity.PlaylistEntityTrackInPlaylistEntityCrossRef
import com.practicum.playlistmaker.mediaLibrary.data.db.entity.TrackInPlaylistEntity

data class TrackEntityWithPlaylists(
    @Embedded val trackInPlaylistEntity: TrackInPlaylistEntity,
    @Relation(
        parentColumn = "remote_track_id",
        entityColumn = "playlist_name",
        associateBy = Junction(PlaylistEntityTrackInPlaylistEntityCrossRef::class)
    )
    val playlists: List<PlaylistEntity>
)