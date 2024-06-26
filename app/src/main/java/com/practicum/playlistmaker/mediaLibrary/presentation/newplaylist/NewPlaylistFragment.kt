package com.practicum.playlistmaker.mediaLibrary.presentation.newplaylist

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.LifecycleOwner
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentNewPlaylistBinding
import com.practicum.playlistmaker.player.presentation.AudioplayerActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

private const val NAME_INPUT_TEXT = "NAME_INPUT_TEXT"
private const val DESCRIPTION_INPUT_TEXT = "DESCRIPTION_INPUT_TEXT"

class NewPlaylistFragment : Fragment() {
    private var _binding: FragmentNewPlaylistBinding? = null
    private val binding
        get() = _binding!!
    private var imageIsEmpty: Boolean = true
    lateinit var confirmDialog: MaterialAlertDialogBuilder
    private var pathToPlaylistImage: String = ""
    private var playlistName: String = ""
    private var playlistDescription: String = ""
    private val viewModel: NewPlaylistViewModel by viewModel<NewPlaylistViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentNewPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.buttonCreatePlaylist.isEnabled = false

        binding.toolbarNewPlaylist.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        val pickAlbumImage =
            registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                if (uri != null) {
                    binding.newPlaylistImagePlaceholder.setImageURI(uri)
                    imageIsEmpty = false
                    pathToPlaylistImage = uri.toString()
                } else {
                    Log.d("PhotoPicker", "No image selected")
                }
            }

        confirmDialog = MaterialAlertDialogBuilder(requireActivity())
            .setTitle(R.string.dialog_title)
            .setMessage(R.string.dialog_message)
            .setNeutralButton(R.string.dialog_cancel) { dialog, which ->

            }.setPositiveButton(R.string.dialog_finalize) { dialog, which ->

                if (requireActivity() is AudioplayerActivity) {
                    requireActivity().supportFragmentManager.beginTransaction().remove(this)
                        .commit()
                } else {
                    parentFragmentManager.popBackStack()
                }
            }

        binding.newPlaylistImagePlaceholder.setOnClickListener {
            pickAlbumImage.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        binding.playlistNameTextInputLayout.editText?.doAfterTextChanged {
            binding.buttonCreatePlaylist.isEnabled = it?.isNotBlank() == true
            playlistName = it?.toString().orEmpty()
        }

        binding.playlistDescriptionTextInputLayout.editText?.doAfterTextChanged {
            playlistDescription = it?.toString().orEmpty()
        }

        if (savedInstanceState != null) {
            binding.playlistNameTextInputLayout.editText?.setText(
                savedInstanceState.getString(
                    NAME_INPUT_TEXT
                )
            )
            binding.playlistDescriptionTextInputLayout.editText?.setText(
                savedInstanceState.getString(
                    DESCRIPTION_INPUT_TEXT
                )
            )
        }

        binding.buttonCreatePlaylist.setOnClickListener {
            viewModel.savePlaylist(
                name = playlistName,
                description = playlistDescription,
                pathToImage = pathToPlaylistImage
            )
        }

        viewModel.playlistIsCreatedState.observe(viewLifecycleOwner) {
            if (it) {
                viewModel.saveImageToAppStorage(
                    playlistImage = pathToPlaylistImage,
                    playlistName = playlistName
                )
                makeToast(resources.getString(R.string.playlist_created_message, playlistName))

                if (requireActivity() is AudioplayerActivity) {
                    requireActivity().supportFragmentManager.beginTransaction()
                        .remove(this@NewPlaylistFragment)
                        .commit()
                } else {
                    parentFragmentManager.popBackStack()
                }
            } else {
                makeToast(resources.getString(R.string.playlist_name_error_message))
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            if (binding.playlistNameTextInputLayout.editText?.text?.isNotEmpty() == true ||
                binding.playlistDescriptionTextInputLayout.editText?.text?.isNotEmpty() == true ||
                !imageIsEmpty
            ) {
                confirmDialog.show()
            } else {
                if (requireActivity() is AudioplayerActivity) {
                    requireActivity().supportFragmentManager.beginTransaction()
                        .remove(this@NewPlaylistFragment)
                        .commit()
                } else {
                    parentFragmentManager.popBackStack()
                }
            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(
            NAME_INPUT_TEXT,
            binding.playlistNameTextInputLayout.editText?.text?.toString()
        )
        outState.putString(
            DESCRIPTION_INPUT_TEXT,
            binding.playlistDescriptionTextInputLayout.editText?.text?.toString()
        )
    }


    private fun makeToast(toastMessage: String) {
        Toast.makeText(requireContext(), toastMessage, Toast.LENGTH_LONG).show()
    }

    companion object {
        fun newInstance() = NewPlaylistFragment()
    }

}