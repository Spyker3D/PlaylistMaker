package com.practicum.playlistmaker.mediaLibrary.presentation.playlistdetailsandedit

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentPlaylistEditBinding
import com.practicum.playlistmaker.player.presentation.AudioplayerActivity
import com.practicum.playlistmaker.player.presentation.KEY_SELECTED_TRACK_DETAILS
import com.practicum.playlistmaker.search.ui.TrackAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class PlaylistDetailsAndEditFragment : Fragment() {

    private var _binding: FragmentPlaylistEditBinding? = null
    private val binding get() = _binding!!
    lateinit var playlistName: String
    lateinit var playlistAdapter: TrackAdapter


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentPlaylistEditBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        var trackDeletionDialog: MaterialAlertDialogBuilder
        var playlistDeletionDialog: MaterialAlertDialogBuilder

        binding.toolbarPlaylistDetails.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        playlistName = arguments?.getString(PLAYLIST_NAME_KEY).toString()

        val viewModel by viewModel<PlaylistDetailsAndEditViewModel> {
            parametersOf(playlistName)
        }
        viewModel.loadPlaylistDetails()
        viewModel.playlistsState.observe(viewLifecycleOwner) {
            if (it is PlaylistDetailsAndEditState.Content) {
                render(it)
            }
        }

        binding.playlistName.text = playlistName

        playlistAdapter = TrackAdapter(
            emptyList(),
            onTrackClickListener = {
                val audioplayerIntent =
                    Intent(requireContext(), AudioplayerActivity::class.java)
                audioplayerIntent.putExtra(KEY_SELECTED_TRACK_DETAILS, it)
                startActivity(audioplayerIntent)
            },
            onLongTrackClickListener = {
                trackDeletionDialog = MaterialAlertDialogBuilder(requireActivity())
                    .setMessage(R.string.dialog_track_deletion_message)
                    .setNegativeButton(R.string.dialog_track_deletion_negative) { dialog, which ->
                        dialog.dismiss()

                    }.setPositiveButton(R.string.dialog_track_deletion_positive) { dialog, which ->
                        viewModel.deleteTrackFromPlaylist(it.trackId, playlistName)
                    }
                trackDeletionDialog.show()
            })

        binding.bottomSheetRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        binding.bottomSheetRecyclerView.adapter = playlistAdapter

        val playlistBottomSheetBehavior =
            BottomSheetBehavior.from(binding.playlistDetails).apply {
                state = BottomSheetBehavior.STATE_HIDDEN
            }

        playlistBottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {

            override fun onStateChanged(bottomSheet: View, newState: Int) {

                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> binding.overlay.isVisible = false
                    else -> binding.overlay.isVisible = true
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                binding.overlay.alpha = slideOffset + 1
            }
        })

        binding.share.setOnClickListener {
            sharePlaylist(viewModel)
        }

        binding.playlistShareTextBottomSheet.setOnClickListener {
            sharePlaylist(viewModel)
            playlistBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }

        binding.more.setOnClickListener {
            playlistBottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        binding.playlistDeleteTextBottomSheet.setOnClickListener {
            playlistDeletionDialog = MaterialAlertDialogBuilder(requireActivity())
                .setMessage(getString(R.string.delete_playlist_message, playlistName))
                .setNegativeButton(R.string.dialog_track_deletion_negative) { dialog, which ->
                    dialog.dismiss()

                }.setPositiveButton(R.string.dialog_track_deletion_positive) { dialog, which ->
                    viewModel.deletePlaylist()
                    parentFragmentManager.popBackStack()
                }
            playlistDeletionDialog.show()
        }

    }

    private fun render(playlistDetailsAndEditState: PlaylistDetailsAndEditState.Content) {
        binding.playlistName.text = playlistDetailsAndEditState.playlist.playlistName
        binding.playlistDescription.text = playlistDetailsAndEditState.playlist.playlistDescription

        Glide.with(requireActivity())
            .load(playlistDetailsAndEditState.playlist.pathToImage)
            .placeholder(R.drawable.placeholder_album)
            .centerCrop()
            .into(binding.playslistImage)

        binding.playlistDuration.text = binding.root.resources.getQuantityString(
            R.plurals.play_list_length,
            playlistDetailsAndEditState.playlistLength,
            playlistDetailsAndEditState.playlistLength
        )
        binding.playlistNumberOfTracks.text = binding.root.resources.getQuantityString(
            R.plurals.number_of_tracks_plurals,
            playlistDetailsAndEditState.playlist.numberOfTracks,
            playlistDetailsAndEditState.playlist.numberOfTracks
        )
        binding.playlistNameBottomSheet.text = playlistDetailsAndEditState.playlist.playlistName
        binding.playlistNumberOfTracksBottomSheet.text = binding.root.resources.getQuantityString(
            R.plurals.number_of_tracks_plurals,
            playlistDetailsAndEditState.playlist.numberOfTracks,
            playlistDetailsAndEditState.playlist.numberOfTracks
        )

        Glide.with(requireActivity())
            .load(playlistDetailsAndEditState.playlist.pathToImage)
            .placeholder(R.drawable.placeholder_track)
            .centerCrop()
            .transform(RoundedCorners(resources.getDimensionPixelSize(R.dimen.playlist_image_rounding_bottomsheet)))
            .into(binding.playlistImageInBottomSheet)

        binding.outOfTracks.isVisible = playlistDetailsAndEditState.tracksList.isEmpty()

        playlistAdapter.updateList { playlistDetailsAndEditState.tracksList }
    }

    private fun sharePlaylist(viewModel: PlaylistDetailsAndEditViewModel) {
        if (playlistAdapter.trackList.isEmpty()) {
            Toast.makeText(
                requireContext(),
                R.string.share_message_no_tracks,
                Toast.LENGTH_LONG
            ).show()
        } else {
            viewModel.sharePlaylist()
        }
    }

    companion object {
        const val PLAYLIST_NAME_KEY = "PLAYLIST_NAME_KEY"
        fun newInstance(playlistName: String) = PlaylistDetailsAndEditFragment().apply {
            arguments = bundleOf(PLAYLIST_NAME_KEY to playlistName)
        }
    }

}