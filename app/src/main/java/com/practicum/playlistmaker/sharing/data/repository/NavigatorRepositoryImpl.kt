package com.practicum.playlistmaker.sharing.data.repository

import android.content.Context
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.sharing.data.externalNavigator.ExternalNavigator
import com.practicum.playlistmaker.sharing.domain.ExternalNavigator.NavigatorRepository
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
}