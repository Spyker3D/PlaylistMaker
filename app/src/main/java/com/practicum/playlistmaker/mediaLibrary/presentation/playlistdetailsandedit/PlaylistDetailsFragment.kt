package com.practicum.playlistmaker.mediaLibrary.presentation.playlistdetailsandedit

import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentPlaylistEditBinding
import com.practicum.playlistmaker.mediaLibrary.presentation.playlists.PlaylistsFragment
import com.practicum.playlistmaker.player.presentation.AudioplayerActivity
import com.practicum.playlistmaker.player.presentation.KEY_SELECTED_TRACK_DETAILS
import com.practicum.playlistmaker.search.ui.TrackAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class PlaylistDetailsFragment : Fragment() {

    private var _binding: FragmentPlaylistEditBinding? = null
    private val binding get() = _binding!!
    private var lastTimeClicked: Long = 0L
    lateinit var playlistName: String
    lateinit var playlistAdapter: TrackAdapter
    private val viewModel by viewModel<PlaylistDetailsViewModel> {
        parametersOf(requireArguments().getString(PLAYLIST_NAME_KEY)!!)
    }
    private var navigatedFromEditPlaylist: Boolean = false


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentPlaylistEditBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        playlistName = requireArguments().getString(PLAYLIST_NAME_KEY)!!

        var trackDeletionDialog: MaterialAlertDialogBuilder
        var playlistDeletionDialog: MaterialAlertDialogBuilder

        binding.toolbarPlaylistDetails.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        viewModel.loadPlaylistDetails()

        viewModel.playlistsState.observe(viewLifecycleOwner) {
            if (it is PlaylistDetailsState.Content) {
                render(it)
            }
        }

        playlistAdapter = TrackAdapter(
            emptyList(),
            onTrackClickListener = {
                val audioplayerIntent =
                    Intent(requireContext(), AudioplayerActivity::class.java)
                audioplayerIntent.putExtra(KEY_SELECTED_TRACK_DETAILS, it)
                startActivity(audioplayerIntent)
            },
            onLongTrackClickListener = {
                trackDeletionDialog =
                    MaterialAlertDialogBuilder(requireActivity(), R.style.MaterialAlertDialog)
                        .setMessage(R.string.dialog_track_deletion_message)
                        .setNegativeButton(R.string.dialog_track_deletion_negative) { dialog, which ->
                            dialog.dismiss()

                        }
                        .setPositiveButton(R.string.dialog_track_deletion_positive) { dialog, which ->
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
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        binding.overlay.isVisible = false
                        binding.playlistTracksBottomSheetContainer.isVisible = true
                    }
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
            binding.playlistTracksBottomSheetContainer.isVisible = false
            playlistBottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        binding.playlistDeleteTextBottomSheet.setOnClickListener {
            playlistDeletionDialog =
                MaterialAlertDialogBuilder(requireActivity(), R.style.MaterialAlertDialog)
                    .setMessage(getString(R.string.delete_playlist_message, playlistName))
                    .setNegativeButton(R.string.dialog_track_deletion_negative) { dialog, which ->
                        dialog.dismiss()

                    }.setPositiveButton(R.string.dialog_track_deletion_positive) { dialog, which ->
                        viewModel.deletePlaylist()
                        parentFragmentManager.popBackStack()
                    }
            playlistDeletionDialog.show()
        }

        binding.playlistEditInfoTextBottomSheet.setOnClickListener {
            openEditPlaylistScreen(playlistName)
        }

    }

    private fun render(playlistDetailsAndEditState: PlaylistDetailsState.Content) {
        binding.playlistName.text = playlistDetailsAndEditState.playlist.playlistNameSecondary
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
        binding.playlistNameBottomSheet.text =
            playlistDetailsAndEditState.playlist.playlistNameSecondary
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

    private fun sharePlaylist(viewModel: PlaylistDetailsViewModel) {
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

    private fun openEditPlaylistScreen(playlistName: String) {
        if (clickDebounce()) {
            navigatedFromEditPlaylist = true
            val bundle = bundleOf(PlaylistsFragment.PLAYLIST_NAME_KEY to playlistName)
            findNavController().navigate(
                R.id.action_playlistDetailsAndEditFragment_to_editPlaylistFragment,
                bundle
            )
        }
    }

    private fun clickDebounce(): Boolean {
        val currentTime = SystemClock.uptimeMillis()
        if (currentTime - lastTimeClicked < EDIT_CLICK_DEBOUNCE_DELAY) return false

        lastTimeClicked = currentTime
        return true
    }

    companion object {
        const val PLAYLIST_NAME_KEY = "playlist_name"
        const val UPDATE_CURRENT_PLAYLIST_KEY = "update_current_playlist"
        const val DATA_KEY = "data"
        const val EDIT_CLICK_DEBOUNCE_DELAY = 200L
    }

}