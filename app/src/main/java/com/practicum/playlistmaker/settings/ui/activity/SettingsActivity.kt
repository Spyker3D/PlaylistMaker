package com.practicum.playlistmaker.settings.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate.NightMode
import androidx.appcompat.widget.SwitchCompat
import androidx.lifecycle.ViewModelProvider
import com.practicum.playlistmaker.databinding.ActivitySettingsBinding
import com.practicum.playlistmaker.settings.ui.viewModel.SettingsViewModel

class SettingsActivity : AppCompatActivity() {

    private lateinit var viewModel: SettingsViewModel
    private lateinit var themeSwitcher: SwitchCompat

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewBinding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        themeSwitcher = viewBinding.themeSwitcher

        viewBinding.toolbarSettings.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        viewModel = ViewModelProvider(
            this,
            SettingsViewModel.getViewModelFactory()
        )[SettingsViewModel::class.java]

        viewModel.isNightModeOnLiveData.observe(this) {
            switchThemeSwitcher(it)
        }

        themeSwitcher.setOnCheckedChangeListener { _, isCheckedStatus ->
            viewModel.switchTheme(isCheckedStatus)
        }

        viewBinding.buttonShare.setOnClickListener {
            viewModel.shareApp()
        }

        viewBinding.buttonSupport.setOnClickListener {
            viewModel.openEmail()
        }

        viewBinding.buttonUserAgreement.setOnClickListener {
            viewModel.openTerms()
        }
    }

    private fun switchThemeSwitcher(isNightModeOn: Boolean) {
        themeSwitcher.isChecked = isNightModeOn
    }
}