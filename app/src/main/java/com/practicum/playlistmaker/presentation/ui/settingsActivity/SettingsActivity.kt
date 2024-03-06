package com.practicum.playlistmaker.presentation.ui.settingsActivity

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.practicum.playlistmaker.App
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewBinding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        val themeSwitcher = viewBinding.themeSwitcher

        viewBinding.toolbarSettings.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        themeSwitcher.isChecked = (applicationContext as App).nightMode

        themeSwitcher.setOnCheckedChangeListener { _, isCheckedStatus ->
            (applicationContext as App).switchTheme(isCheckedStatus)
        }

        viewBinding.buttonShare.setOnClickListener {
            Intent().apply {
                val message = getString(R.string.link_to_android_developer_course)
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, message)
                type = "text/plain"
                Intent.createChooser(this, null)
                startActivity(this)
            }
        }

        viewBinding.buttonSupport.setOnClickListener {
            Intent().apply {
                val title = getString(R.string.support_email_title)
                val message = getString(R.string.support_email_message)
                action = Intent.ACTION_SENDTO
                data = Uri.parse("mailto:")
                putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.students_email)))
                putExtra(Intent.EXTRA_SUBJECT, title)
                putExtra(Intent.EXTRA_TEXT, message)
                startActivity(this)
            }
        }

        viewBinding.buttonUserAgreement.setOnClickListener {
            val url = Uri.parse(getString(R.string.practicum_offer))
            val intent = Intent(Intent.ACTION_VIEW, url)
            startActivity(intent)
        }
    }
}