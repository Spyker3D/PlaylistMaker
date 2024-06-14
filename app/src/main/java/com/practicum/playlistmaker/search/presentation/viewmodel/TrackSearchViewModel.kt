package com.practicum.playlistmaker.search.presentation.viewmodel

import android.app.Application
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.di.viewModelModule
import com.practicum.playlistmaker.search.domain.entities.Resource
import com.practicum.playlistmaker.search.domain.entities.TrackInfo
import com.practicum.playlistmaker.search.domain.interactor.GetHistoryTrackUseCase
import com.practicum.playlistmaker.search.domain.interactor.SaveHistoryTrackUseCase
import com.practicum.playlistmaker.search.domain.interactor.SearchTrackUseCase
import com.practicum.playlistmaker.search.presentation.entities.SearchState
import com.practicum.playlistmaker.search.presentation.entities.Track
import com.practicum.playlistmaker.search.presentation.mapper.TrackPresentationMapper
import com.practicum.playlistmaker.search.presentation.mapper.TrackPresentationMapper.mapToDomain
import com.practicum.playlistmaker.search.presentation.mapper.TrackPresentationMapper.mapToPresentation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.yield
import kotlin.system.measureTimeMillis

class TrackSearchViewModel(
    application: Application,
    private val searchTrackUseCase: SearchTrackUseCase,
    private val saveHistoryTrackUseCase: SaveHistoryTrackUseCase,
    private val getHistoryTrackUseCase: GetHistoryTrackUseCase,
) :
    AndroidViewModel(application) {

    private val _stateLiveData =
        MutableLiveData<SearchState>()

    val stateLiveData: LiveData<SearchState> = _stateLiveData
    private val handler = Handler(Looper.getMainLooper())

    private var searchJob: Job? = null
    private var showHistoryJob: Job? = null

    private var latestSearchText: String? = null

    init {
        viewModelScope.launch {
            _stateLiveData.value = SearchState.HistoryListPresentation(getHistoryList())
        }
    }

    fun searchDebounce(changedText: String?, forceSearch: Boolean) {

        if (!forceSearch && latestSearchText == changedText) return

        searchJob?.cancel()

        if (changedText?.isBlank() != false) {
            this.latestSearchText = changedText
            return
        }

        this.latestSearchText = changedText

        searchJob = viewModelScope.launch {
            delay(SEARCH_DEBOUNCE_DELAY)
            searchRequest(changedText)
        }
    }

    private suspend fun searchRequest(newSearchText: String) {

        if (newSearchText.isNotEmpty()) {
            renderState(SearchState.Loading)
        }

        searchTrackUseCase.execute(newSearchText).collect {
            processResult(it)
        }
    }

    private fun processResult(searchResult: Resource<List<TrackInfo>?>) {

        if (searchResult.data != null) {
            when {
                searchResult.data.isEmpty() -> renderState(
                    SearchState.Empty(
                        getApplication<Application>().getString(R.string.nothing_found)
                    )
                )

                else -> {
                    renderState(
                        SearchState.Content(
                            searchResult.data.map { it.mapToPresentation() }
                        )
                    )
                }
            }
        } else {
            when (searchResult) {
                is Resource.Error -> {
                    renderState(
                        SearchState.Error(
                            getApplication<Application>().getString(R.string.server_error)
                        )
                    )
                }

                is Resource.InternetConnectionError -> {

                    renderState(
                        SearchState.InternetConnectionError(
                            getApplication<Application>().getString(R.string.search_error_message)
                        )
                    )
                }

                is Resource.Success -> Unit
            }
        }
    }

    private fun renderState(state: SearchState) {
        _stateLiveData.postValue(state)
    }

    fun addTrackToHistoryList(trackToSave: Track) {
        viewModelScope.launch {
            val filteredList = getHistoryList() - trackToSave
            val updatedList = listOf(trackToSave) + filteredList
            val finalUpdatedList =
                if (updatedList.size > 10) updatedList.subList(0, 10) else updatedList
            saveHistoryTrackUseCase.execute(finalUpdatedList.map { it.mapToDomain() })
        }
    }

    fun onTextChanged(s: String?, editTextHasFocus: Boolean) {
        /* renderState can only be called from this method, because otherwise it can be called when
        it is not relevant anymore */
        showHistoryJob?.cancel()

        searchDebounce(changedText = s, forceSearch = false)
        if (editTextHasFocus && s?.isBlank() != false) {
            showHistoryJob = viewModelScope.launch {
                renderState(SearchState.HistoryListPresentation(getHistoryList()))
            }
        }
    }

    private suspend fun getHistoryList(): List<Track> {
        return (getHistoryTrackUseCase.execute().map { it.mapToPresentation() })
    }

    fun clearHistoryList() {
        val historyList = listOf<TrackInfo>()
        saveHistoryTrackUseCase.execute(historyList)
    }

    override fun onCleared() {
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)
    }

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private val SEARCH_REQUEST_TOKEN = Any()
    }
}
