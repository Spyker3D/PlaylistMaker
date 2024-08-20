package com.practicum.playlistmaker.search.domain.interactor

import com.practicum.playlistmaker.search.domain.entities.Resource
import com.practicum.playlistmaker.search.domain.entities.TrackInfo
import com.practicum.playlistmaker.search.domain.repository.TrackSearchRepository
import kotlinx.coroutines.flow.Flow

class SearchTrackUseCase(private val trackSearchRepository: TrackSearchRepository) {

    fun execute(trackName: String): Flow<Resource<List<TrackInfo>?>> {
        return trackSearchRepository.searchTrack(trackName)
    }

}