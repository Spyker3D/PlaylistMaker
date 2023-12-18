package com.practicum.playlistmaker

import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SwitchCompat
import androidx.appcompat.widget.Toolbar
import com.practicum.playlistmaker.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val switchNightMode = binding.switcherNightTheme

        binding.toolbarSettings.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        val sharedPreferences =
            getSharedPreferences("com.practicum.playlistmaker.MY_PREFS", MODE_PRIVATE)

        switchNightMode.setOnCheckedChangeListener { _, isCheckedStatus ->
            sharedPreferences
                .edit()
                .putBoolean("isNightModeOn", isCheckedStatus)
                .apply()
            if (isCheckedStatus) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }

        switchNightMode.isChecked = sharedPreferences.getBoolean("isNightModeOn", false)

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