package com.practicum.playlistmaker.mediaLibrary.presentation.editplaylist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.mediaLibrary.domain.entities.Playlist
import com.practicum.playlistmaker.mediaLibrary.domain.interactor.PlaylistInteractor
import com.practicum.playlistmaker.mediaLibrary.presentation.newplaylist.NewPlaylistViewModel
import com.practicum.playlistmaker.search.presentation.entities.Track
import com.practicum.playlistmaker.search.presentation.mapper.TrackPresentationMapper.mapToPresentation
import kotlinx.coroutines.launch

class EditPlaylistViewModel(
    private val playlistName: String,
    private val playlistInteractor: PlaylistInteractor,
) : NewPlaylistViewModel(playlistInteractor) {

    private val _playListState = MutableLiveData<EditPlayListState>()
    val playlistState: LiveData<EditPlayListState> = _playListState

    init {
        viewModelScope.launch {
            val playlist = playlistInteractor.getPlaylistByName(playlistName)
            _playListState.value = EditPlayListState(
                playlistName = playlist.playlistName,
                playlistDescription = playlist.playlistDescription,
                playlistImage = playlist.pathToImage
            )
        }
    }

    fun updatedata() {
        viewModelScope.launch {
            val playlist = playlistInteractor.getPlaylistByName(playlistName)
            _playListState.value = EditPlayListState(
                playlistName = playlist.playlistName,
                playlistDescription = playlist.playlistDescription,
                playlistImage = playlist.pathToImage
            )
        }
    }

    fun updatePlaylistWithTheSameName(playlistDescriptionNew: String?, playlistImageNew: String?) {
        viewModelScope.launch {
            val initialPlaylist = playlistInteractor.getPlaylistByName(playlistName)

            val newPlaylist = Playlist(
                playlistName = playlistName,
                playlistDescription = playlistDescriptionNew,
                numberOfTracks = initialPlaylist.numberOfTracks,
                pathToImage = playlistImageNew
            )
            playlistInteractor.updateExistingPlaylist(
                newPlaylist.playlistName,
                newPlaylist.playlistDescription,
                newPlaylist.numberOfTracks,
                newPlaylist.pathToImage
            )

        }
    }

//    fun updatePlaylistWithNewName(
//        playlistNameNew: String,
//        playlistDescription: String?,
//        playlistImage: String?,
//    ) {
//        viewModelScope.launch {
//            val initialPlaylist = playlistInteractor.getPlaylistByName(playlistName)
//
//            val pairOfPlaylistAndTracks = playlistInteractor.getAllPlaylistDetails(playlistName)
//            val tracksList: List<Track> =
//                pairOfPlaylistAndTracks.second.map { it.mapToPresentation() }
//
//            val newPlaylist = Playlist(
//                playlistName = playlistNameNew,
//                playlistDescription = playlistDescription,
//                numberOfTracks = initialPlaylist.numberOfTracks,
//                pathToImage = playlistImage
//            )
//            playlistInteractor.insertPlaylist(newPlaylist)
//
//            for (trackInfo in tracksList) {
//                playlistInteractor.addTrackToPlaylist(track = trackInfo, playlist = newPlaylist)
//            }
//
//            playlistInteractor.deletePlaylist(initialPlaylist)
//        }
//    }


}