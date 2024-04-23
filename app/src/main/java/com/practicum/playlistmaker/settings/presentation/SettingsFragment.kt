package com.practicum.playlistmaker.settings.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.practicum.playlistmaker.databinding.FragmentSettingsBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewModel: SettingsViewModel by viewModel<SettingsViewModel>()

        viewModel.isNightModeOnLiveData.observe(viewLifecycleOwner) {
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}