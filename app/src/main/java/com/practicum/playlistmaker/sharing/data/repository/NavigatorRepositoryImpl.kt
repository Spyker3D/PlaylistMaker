package com.practicum.playlistmaker.sharing.data.repository

import android.content.Context
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.mediaLibrary.domain.entities.Playlist
import com.practicum.playlistmaker.search.presentation.entities.Track
import com.practicum.playlistmaker.sharing.data.externalNavigator.ExternalNavigator
import com.practicum.playlistmaker.sharing.domain.externalNavigator.NavigatorRepository
import com.practicum.playlistmaker.sharing.domain.entities.EmailData

class NavigatorRepositoryImpl(
    private val externalNavigator: ExternalNavigator,
    private val context: Context,
) : NavigatorRepository {
    override fun shareApp() {
        externalNavigator.shareLink(context.getString(R.string.link_to_android_developer_course))
    }

    override fun openTerms() {
        externalNavigator.openLink(context.getString(R.string.practicum_offer))
    }

    override fun openEmail() {
        externalNavigator.openEmail(
            EmailData(
                title = context.getString(R.string.support_email_title),
                text = context.getString(R.string.support_email_message),
                emailAddress = context.getString(R.string.students_email)
            )
        )
    }

    override fun sharePlaylist(playlist: Playlist, trackList: List<Track>) {
        val textToShare =
            "${playlist.playlistName}\n${playlist.playlistDescription}\n${
                context.resources.getQuantityString(
                    R.plurals.number_of_tracks_plurals,
                    playlist.numberOfTracks,
                    playlist.numberOfTracks
                )
            }\n"
        var trackListText = ""
        for (track in trackList) {
            trackListText += "${trackList.indexOf(track) + 1}.${track.artistName} - ${track.trackName} (${track.trackTimeMillisFormatted})\n"
        }
        return externalNavigator.sharePlaylist(textToShare + trackListText)
    }

}