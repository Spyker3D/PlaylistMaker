package com.practicum.playlistmaker.settings.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.practicum.playlistmaker.databinding.ActivitySettingsBinding
import com.practicum.playlistmaker.settings.ui.viewModel.SettingsViewModel

class SettingsActivity : AppCompatActivity() {

    private lateinit var viewModel: SettingsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewBinding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        val themeSwitcher = viewBinding.themeSwitcher

        viewBinding.toolbarSettings.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        viewModel = ViewModelProvider(
            this,
            SettingsViewModel.getViewModelFactory()
        )[SettingsViewModel::class.java]

        themeSwitcher.isChecked = viewModel.getIsNightModeOn()

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
}