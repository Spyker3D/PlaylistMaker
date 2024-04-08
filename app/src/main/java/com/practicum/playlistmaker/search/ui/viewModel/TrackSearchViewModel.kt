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
import com.practicum.playlistmaker.search.domain.consumer.ConsumerData
import com.practicum.playlistmaker.search.domain.entities.TrackInfo
import com.practicum.playlistmaker.search.ui.entities.SearchState

class TrackSearchViewModel(application: Application) : AndroidViewModel(application) {

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private val SEARCH_REQUEST_TOKEN = Any()

        fun getViewModelFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                TrackSearchViewModel(this[APPLICATION_KEY] as Application)
            }
        }
    }

    private val searchTrackUseCase by lazy { Creator.provideSearchTrackUseCase() }
    private val saveHistoryTrackUseCase by lazy { Creator.provideSaveHistoryTrackUseCase() }
    private val getHistoryTrackUseCase by lazy { Creator.provideGetHistoryTrackUseCase() }
    private val selectedTrackInteractor by lazy { Creator.provideSelectedTrackInteractor() }

    private val stateLiveData = MutableLiveData<SearchState>()
    fun observeState(): LiveData<SearchState> = stateLiveData

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
            trackName = newSearchText
        ) { data ->
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
                        SearchState.Error(
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
                            SearchState.Error(
                                getApplication<Application>().getString(
                                    R.string.nothing_found
                                )
                            )
                        )
                    } else {
                        renderState(SearchState.Content(trackListFound))
                    }
                }
            }
        }
    }

    private fun renderState(state: SearchState) {
        stateLiveData.postValue(state)
    }

    fun addTrackToHistoryList(trackToSave: TrackInfo) {
        val filteredList = getHistoryList() - trackToSave
        val updatedList = listOf(trackToSave) + filteredList
        val finalUpdatedList =
            if (updatedList.size > 10) updatedList.subList(0, 10) else updatedList
        saveHistoryTrackUseCase.execute(finalUpdatedList)
    }

    fun showHistoryList() {
        val historyList = getHistoryList()
        renderState(SearchState.HistoryListPresentation(historyList))
    }

    fun getHistoryList(): List<TrackInfo> {
        return getHistoryTrackUseCase.execute()
    }

    fun removeCallbacks() {
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)
    }

    fun clearHistoryList() {
        var historyList = listOf<TrackInfo>()
        saveHistoryTrackUseCase.execute(historyList)
    }

    fun saveSelectedTrack(track: TrackInfo) {
        selectedTrackInteractor.saveSelectedTrack(track)
    }

    override fun onCleared() {
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)
    }
}