package com.practicum.playlistmaker.mediaLibrary.presentation.editplaylist

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.addCallback
import androidx.core.net.toUri
import androidx.core.os.bundleOf
import androidx.core.widget.doAfterTextChanged
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.mediaLibrary.presentation.newplaylist.NewPlaylistFragment
import com.practicum.playlistmaker.player.presentation.AudioplayerActivity
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class EditPlaylistFragment : NewPlaylistFragment() {

    override val viewModel by viewModel<EditPlaylistViewModel> {
        parametersOf(requireArguments().getString(PLAYLIST_NAME_KEY))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var initialPlaylistName: String? = null
        var initialPlaylistDescription: String? = null
        var initialImagePath: String? = null

        var adjustedlPlaylistName: String? = null
        var adjustedPlaylistDescription: String? = null
        var adjustedImagePath: String? = null
        if (pathToPlaylistImage != "") {
            adjustedImagePath = pathToPlaylistImage
        }

        binding.toolbarNewPlaylist.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            parentFragmentManager.popBackStack()
        }

        binding.buttonCreatePlaylist.text = getString(R.string.save_changes)
        binding.toolbarNewPlaylist.title = getString(R.string.edit_title_for_toolbar)

        viewModel.playlistState.observe(viewLifecycleOwner) {
            binding.playlistNameEditText.setText(it.playlistName)
            binding.playlistDescriptionEditText.setText(it.playlistDescription)
//            if(it.playlistImage?.isNotEmpty() == true) {
//                binding.newPlaylistImagePlaceholder.setImageURI(it.playlistImage.toUri())
//            } else {
//                binding.newPlaylistImagePlaceholder.getIm(R.drawable.placeholder_album)
//            }

            Glide.with(this)
                .load(it.playlistImage)
                .placeholder(R.drawable.placeholder_album)
                .centerCrop()
                .into(binding.newPlaylistImagePlaceholder)

            initialPlaylistName = it.playlistName
            initialPlaylistDescription = it.playlistDescription
            initialImagePath = it.playlistImage
        }

        binding.playlistNameTextInputLayout.editText?.doAfterTextChanged {
            binding.buttonCreatePlaylist.isEnabled = it?.isNotBlank() == true
            adjustedlPlaylistName = it?.toString().orEmpty()

        }

        binding.playlistDescriptionTextInputLayout.editText?.doAfterTextChanged {
            adjustedPlaylistDescription = it?.toString().orEmpty()

        }

        binding.buttonCreatePlaylist.setOnClickListener {
//            if (adjustedlPlaylistName == initialPlaylistName
//                && adjustedPlaylistDescription == initialPlaylistDescription
//                && adjustedImagePath == initialImagePath) {
//
//            }

            if (binding.playlistNameEditText.text?.isNotEmpty() == true) {

                parentFragmentManager.setFragmentResult(
                    UPDATE_CURRENT_PLAYLIST_KEY,
                    bundleOf(DATA_KEY to "$adjustedlPlaylistName")
                )

//                if (adjustedlPlaylistName != initialPlaylistName) {

//                    viewModel.updatePlaylistWithNewName(
//                        adjustedlPlaylistName!!,
//                        adjustedPlaylistDescription,
//                        adjustedImagePath
//                    )
//                    viewModel.updatedata()
//                    parentFragmentManager.popBackStack()
//                } else {
                    viewModel.updatePlaylistWithTheSameName(
                        adjustedPlaylistDescription,
                        adjustedImagePath
                    )
                    viewModel.updatedata()
                    parentFragmentManager.popBackStack()
//                }
            }
        }

    }


    companion object {
        const val PLAYLIST_NAME_KEY = "playlist_name"
        const val UPDATE_CURRENT_PLAYLIST_KEY = "update_current_playlist"
        const val DATA_KEY = "data"
    }

}