package com.practicum.playlistmaker

import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import com.practicum.playlistmaker.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val themeSwitcher = binding.themeSwitcher

        binding.toolbarSettings.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        themeSwitcher.isChecked = (applicationContext as App).nightMode

        themeSwitcher.setOnCheckedChangeListener { _, isCheckedStatus ->
            (applicationContext as App).switchTheme(isCheckedStatus)
        }

        binding.buttonShare.setOnClickListener {
            Intent().apply {
                val message = getString(R.string.link_to_android_developer_course)
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, message)
                type = "text/plain"
                Intent.createChooser(this, null)
                startActivity(this)
            }
        }

        binding.buttonSupport.setOnClickListener {
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

        binding.buttonUserAgreement.setOnClickListener {
            val url = Uri.parse(getString(R.string.practicum_offer))
            val intent = Intent(Intent.ACTION_VIEW, url)
            startActivity(intent)
        }
    }
}