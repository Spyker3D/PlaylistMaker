package com.practicum.playlistmaker.search.ui.viewModel

import android.app.Application
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.creator.Creator
import com.practicum.playlistmaker.search.domain.consumer.Consumer
import com.practicum.playlistmaker.search.domain.consumer.ConsumerData
import com.practicum.playlistmaker.search.domain.entities.TrackInfo
import com.practicum.playlistmaker.search.domain.interactor.GetHistoryTrackUseCase
import com.practicum.playlistmaker.search.domain.interactor.SaveHistoryTrackUseCase
import com.practicum.playlistmaker.search.domain.interactor.SaveSelectedTrackUseCase
import com.practicum.playlistmaker.search.domain.interactor.SearchTrackUseCase
import com.practicum.playlistmaker.search.ui.entities.SearchState
import com.practicum.playlistmaker.search.ui.entities.Track
import com.practicum.playlistmaker.search.ui.mapper.TrackPresentationMapper

class TrackSearchViewModel(
    application: Application,
    private val searchTrackUseCase: SearchTrackUseCase,
    private val saveHistoryTrackUseCase: SaveHistoryTrackUseCase,
    private val getHistoryTrackUseCase: GetHistoryTrackUseCase,
    private val saveSelectedTrackUseCase: SaveSelectedTrackUseCase,
) :
    AndroidViewModel(application) {

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private val SEARCH_REQUEST_TOKEN = Any()

        fun getViewModelFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {

                TrackSearchViewModel(
                    application = this[APPLICATION_KEY] as Application,
                    searchTrackUseCase = Creator.provideSearchTrackUseCase(),
                    saveHistoryTrackUseCase = Creator.provideSaveHistoryTrackUseCase(),
                    getHistoryTrackUseCase = Creator.provideGetHistoryTrackUseCase(),
                    saveSelectedTrackUseCase = Creator.provideSelectedTrackInteractor()
                )
            }
        }
    }

    private val _stateLiveData =
        MutableLiveData<SearchState>(SearchState.HistoryListPresentation(getHistoryList()))
    val stateLiveData: LiveData<SearchState> = _stateLiveData

    private val handler = Handler(Looper.getMainLooper())
    private var latestSearchText: String? = null

    fun searchDebounce(changedText: String, forceSearch: Boolean) {

        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)

        if (changedText.isBlank()) {
            handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)
            this.latestSearchText = changedText
            searchTrackUseCase.cancelRequest()
            return
        }

        if (!forceSearch && latestSearchText == changedText) return

        this.latestSearchText = changedText

        val searchRunnable = Runnable { searchRequest(changedText) }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            handler.postDelayed(searchRunnable, SEARCH_REQUEST_TOKEN, SEARCH_DEBOUNCE_DELAY)
        } else {
            val postTime = SystemClock.uptimeMillis() + SEARCH_DEBOUNCE_DELAY
            handler.postAtTime(searchRunnable, SEARCH_REQUEST_TOKEN, postTime)
        }
    }

    private fun searchRequest(newSearchText: String) {

        if (newSearchText.isNotEmpty()) {
            renderState(SearchState.Loading)
        }

        searchTrackUseCase.execute(
            trackName = newSearchText,
            object : Consumer<List<TrackInfo>> {
                override fun consume(data: ConsumerData<List<TrackInfo>>) {
                    when (data) {
                        is ConsumerData.Error -> {
                            renderState(
                                SearchState.Error(
                                    getApplication<Application>().getString(
                                        R.string.server_error
                                    )
                                )
                            )
                        }

                        is ConsumerData.InternetConnectionError -> {
                            renderState(
                                SearchState.InternetConnectionError(
                                    getApplication<Application>().getString(
                                        R.string.search_error_message
                                    )
                                )
                            )
                        }

                        is ConsumerData.Data -> {
                            val trackListFound = data.value ?: listOf<TrackInfo>()
                            if (trackListFound.isEmpty()) {
                                renderState(
                                    SearchState.Empty(
                                        getApplication<Application>().getString(
                                            R.string.nothing_found
                                        )
                                    )
                                )
                            } else {
                                renderState(
                                    SearchState.Content(
                                        TrackPresentationMapper.mapToPresentation(trackListFound)
                                    )
                                )
                            }
                        }
                    }
                }
            }
        )
    }

    private fun renderState(state: SearchState) {
        _stateLiveData.postValue(state)
    }

    fun addTrackToHistoryList(trackToSave: Track) {
        val filteredList = getHistoryList() - trackToSave
        val updatedList = listOf(trackToSave) + filteredList
        val finalUpdatedList =
            if (updatedList.size > 10) updatedList.subList(0, 10) else updatedList
        saveHistoryTrackUseCase.execute(TrackPresentationMapper.mapToDomain(finalUpdatedList))
    }

    fun showHistoryList() {
        val historyList = getHistoryList()
        renderState(SearchState.HistoryListPresentation(historyList))
    }

    fun onTextChanged(s: String?, editTexthasFocus: Boolean) {

        if (s?.isNotBlank() == true) {
            searchDebounce(changedText = s, forceSearch = false)
        } else if (editTexthasFocus) {
            removeCallbacks()
            renderState(SearchState.HistoryListPresentation(getHistoryList()))
        }
    }

    private fun getHistoryList(): List<Track> {
        return TrackPresentationMapper.mapToPresentation(getHistoryTrackUseCase.execute())
    }

    private fun removeCallbacks() {
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)
    }

    fun clearHistoryList() {
        val historyList = listOf<TrackInfo>()
        saveHistoryTrackUseCase.execute(historyList)
    }

    fun saveSelectedTrack(track: Track) {
        this.saveSelectedTrackUseCase.saveSelectedTrack(
            TrackPresentationMapper.mapToDomain(listOf(track)).first()
        )
    }

    override fun onCleared() {
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)
    }
}
