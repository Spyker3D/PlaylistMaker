package com.practicum.playlistmaker.mediaLibrary.presentation.favoritetracks

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.mediaLibrary.domain.interactor.FavouriteTracksInteractor
import com.practicum.playlistmaker.search.presentation.entities.Track
import com.practicum.playlistmaker.search.presentation.mapper.TrackPresentationMapper.mapToPresentation
import kotlinx.coroutines.launch

class FavoriteTracksViewModel(private val favouriteTracksInteractor: FavouriteTracksInteractor) :
    ViewModel() {

    private val _isFavouriteTracksListFilled = MutableLiveData<List<Track>>()
    val isFavouriteTracksListFilled: LiveData<List<Track>> =
        _isFavouriteTracksListFilled

    fun updateFavouriteList() {
        viewModelScope.launch {
            favouriteTracksInteractor.getFavouriteTracks().collect {trackInfoList ->
                _isFavouriteTracksListFilled.value = trackInfoList.map { it.mapToPresentation() }
            }
        }
    }

}