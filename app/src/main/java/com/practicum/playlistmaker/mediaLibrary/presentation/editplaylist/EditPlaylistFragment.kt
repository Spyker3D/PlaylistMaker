package com.practicum.playlistmaker.mediaLibrary.presentation.editplaylist

import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.addCallback
import androidx.core.widget.doAfterTextChanged
import com.bumptech.glide.Glide
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.mediaLibrary.presentation.newplaylist.NewPlaylistFragment
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class EditPlaylistFragment : NewPlaylistFragment() {

    override val viewModel by viewModel<EditPlaylistViewModel> {
        parametersOf(requireArguments().getString(PLAYLIST_NAME_KEY))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var adjustedlPlaylistName: String? = null
        var adjustedPlaylistDescription: String? = null
        var adjustedImagePath: Uri? = null

        binding.toolbarNewPlaylist.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            parentFragmentManager.popBackStack()
        }

        binding.buttonCreatePlaylist.text = getString(R.string.save_changes)
        binding.toolbarNewPlaylist.title = getString(R.string.edit_title_for_toolbar)

        viewModel.playlistState.observe(viewLifecycleOwner) {
            binding.playlistNameEditText.setText(it.playlistNameSecondary)
            binding.playlistDescriptionEditText.setText(it.playlistDescription)

            Glide.with(this)
                .load(it.playlistImage)
                .placeholder(R.drawable.placeholder_album)
                .centerCrop()
                .into(binding.newPlaylistImagePlaceholder)

        }

        binding.playlistNameTextInputLayout.editText?.doAfterTextChanged {
            binding.buttonCreatePlaylist.isEnabled = it?.isNotBlank() == true
            adjustedlPlaylistName = it?.toString().orEmpty()

        }

        binding.playlistDescriptionTextInputLayout.editText?.doAfterTextChanged {
            adjustedPlaylistDescription = it?.toString().orEmpty()

        }

        binding.buttonCreatePlaylist.setOnClickListener {

            if (binding.playlistNameEditText.text?.isNotEmpty() == true) {
                adjustedImagePath = pathToPlaylistImage

                viewModel.updatePlaylist(
                    adjustedlPlaylistName!!,
                    adjustedPlaylistDescription,
                    adjustedImagePath
                )
            }
        }

        viewModel.closeScreen.observe(viewLifecycleOwner) {
            parentFragmentManager.popBackStack()

        }

    }

    companion object {
        const val PLAYLIST_NAME_KEY = "playlist_name"
    }

}