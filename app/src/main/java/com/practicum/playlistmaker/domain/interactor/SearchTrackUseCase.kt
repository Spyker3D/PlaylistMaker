package com.practicum.playlistmaker.domain.interactor

import com.practicum.playlistmaker.domain.consumer.Consumer
import com.practicum.playlistmaker.domain.consumer.ConsumerData
import com.practicum.playlistmaker.domain.entities.Resource
import com.practicum.playlistmaker.domain.entities.TrackInfo
import com.practicum.playlistmaker.domain.repository.TrackSearchRepository
import java.util.concurrent.Executors

class SearchTrackUseCase(private val trackSearchRepository: TrackSearchRepository) {

    private val executor = Executors.newCachedThreadPool()

    fun execute(trackName: String, consumer: Consumer<List<TrackInfo>>) {
        executor.execute {

            when (val trackList = trackSearchRepository.searchTrack(trackName)) {
                is Resource.Success -> consumer.consume(ConsumerData.Data(trackList.data))

                is Resource.Error -> consumer.consume(ConsumerData.Error())

                is Resource.InternetConnectionError -> {
                    consumer.consume(ConsumerData.InternetConnectionError())
                }
            }
        }
    }

}