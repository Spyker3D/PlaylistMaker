package com.practicum.playlistmaker.settings.presentation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.practicum.playlistmaker.databinding.ActivitySettingsBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val viewModel: SettingsViewModel by viewModel<SettingsViewModel>()

        binding.toolbarSettings.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        viewModel.isNightModeOnLiveData.observe(this) {
            switchThemeSwitcher(it)
        }

        binding.themeSwitcher.setOnCheckedChangeListener { _, isCheckedStatus ->
            viewModel.switchTheme(isCheckedStatus)
        }

        binding.buttonShare.setOnClickListener {
            viewModel.shareApp()
        }

        binding.buttonSupport.setOnClickListener {
            viewModel.openEmail()
        }

        binding.buttonUserAgreement.setOnClickListener {
            viewModel.openTerms()
        }
    }

    private fun switchThemeSwitcher(isNightModeOn: Boolean) {
        binding.themeSwitcher.isChecked = isNightModeOn
    }
}