package com.practicum.playlistmaker.search.domain.interactor

import com.practicum.playlistmaker.search.domain.consumer.Consumer
import com.practicum.playlistmaker.search.domain.consumer.ConsumerData
import com.practicum.playlistmaker.search.domain.entities.Resource
import com.practicum.playlistmaker.search.domain.entities.TrackInfo
import com.practicum.playlistmaker.search.domain.repository.TrackSearchRepository
import com.practicum.playlistmaker.search.presentation.entities.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.concurrent.Executors
import java.util.concurrent.Future

class SearchTrackUseCase(private val trackSearchRepository: TrackSearchRepository) {

    fun execute(trackName: String): Flow<Resource<List<TrackInfo>?>> {
        return trackSearchRepository.searchTrack(trackName)
//            .map {
//            result ->
//            when(result) {
//                is Resource.Success -> Pair(result.data, null)
//
//                is Resource.Error -> Pair(null, result.message)// переделать на сообщения?
//
//                is Resource.InternetConnectionError -> Pair(null, result.message)// лишее оставить просто Error
//            }
//        }
    }
}