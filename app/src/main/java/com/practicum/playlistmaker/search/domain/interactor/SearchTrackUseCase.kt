package com.practicum.playlistmaker.search.domain.interactor

import com.practicum.playlistmaker.search.domain.consumer.ConsumerData
import com.practicum.playlistmaker.search.domain.entities.Resource
import com.practicum.playlistmaker.search.domain.entities.TrackInfo
import com.practicum.playlistmaker.search.domain.repository.TrackSearchRepository
import java.util.concurrent.Executors
import androidx.core.util.Consumer
import java.util.concurrent.Future

class SearchTrackUseCase(private val trackSearchRepository: TrackSearchRepository) {

    private val executor = Executors.newCachedThreadPool()
    private var currentRequestFuture: Future<*>? = null

    fun execute(trackName: String, consumer: Consumer<ConsumerData<List<TrackInfo>?>>) {

        cancelRequest()

        currentRequestFuture = executor.submit {
            val trackList = trackSearchRepository.searchTrack(trackName)
            if(Thread.currentThread().isInterrupted) return@submit
            when (trackList) {
                is Resource.Success -> consumer.accept(ConsumerData.Data(trackList.data))

                is Resource.Error -> consumer.accept(ConsumerData.Error())

                is Resource.InternetConnectionError -> {
                    consumer.accept(ConsumerData.InternetConnectionError())
                }
            }
        }
    }

    fun cancelRequest() {
        currentRequestFuture?.cancel(true)
    }
}