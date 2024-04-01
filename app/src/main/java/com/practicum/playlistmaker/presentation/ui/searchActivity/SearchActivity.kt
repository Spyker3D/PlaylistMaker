package com.practicum.playlistmaker.presentation.ui.searchActivity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.creator.Creator
import com.practicum.playlistmaker.databinding.ActivitySearchBinding
//import com.practicum.playlistmaker.domain.consumer.Consumer
import androidx.core.util.Consumer
import com.practicum.playlistmaker.domain.consumer.ConsumerData
import com.practicum.playlistmaker.domain.entities.TrackInfo
import com.practicum.playlistmaker.presentation.ui.audioPlayer.AudioplayerActivity
import com.practicum.playlistmaker.presentation.ui.audioPlayer.KEY_SELECTED_TRACK_DETAILS

private const val KEY_INPUT_TEXT = "INPUT_TEXT"
private const val KEY_SEARCH_TRACKLIST = "TRACKLIST_SEARCH"
private const val KEY_SEARCH_ACTIVITY_ERROR_STATE = "SEARCH_ACTIVITY_ERROR_STATE"
private const val TRACK_CLICK_DEBOUNCE_DELAY = 1000L
private const val SEARCH_DEBOUNCE_DELAY = 2000L

class SearchActivity : AppCompatActivity() {

    private var text: String = ""
    private var isClickAllowed = true

    private val handler = Handler(Looper.getMainLooper())
    private var useCaseRunnable: Runnable? = null // Runnable { search() }
    private val isClickAllowedRunnable = Runnable { isClickAllowed = true }
    private val searchRunnable = Runnable { searchTrack() }
    private var searchActivityErrorState: SearchActivityError = SearchActivityError.NO_ERROR

    private lateinit var trackSearchAdapter: TrackAdapter
    private lateinit var trackHistoryAdapter: TrackAdapter
    private lateinit var binding: ActivitySearchBinding
    private lateinit var updateButton: Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var imagePlaceholder: ImageView
    private lateinit var textPlaceholder: TextView

    private val creator by lazy { Creator(applicationContext) }
    private val searchTrackUseCase by lazy { creator.provideSearchTrackUseCase() }
    private val saveHistoryTrackUseCase by lazy { creator.provideSaveHistoryTrackUseCase() }
    private val getHistoryTrackUseCase by lazy { creator.provideGetHistoryTrackUseCase() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbarSearch.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        imagePlaceholder = binding.imagePlaceholder
        textPlaceholder = binding.textPlaceholder

        recyclerView = binding.searchRecyclerView
        recyclerView.layoutManager =
            LinearLayoutManager(this@SearchActivity, LinearLayoutManager.VERTICAL, false)

        val youSearchedText: TextView = binding.youSearchedText

        trackHistoryAdapter = TrackAdapter(
            getHistoryTrackUseCase.execute(),
            onTrackClickListener = { setOnTrackClickListenerLogic(it) },
            onActionButtonClickListener = {
                setupClearHistoryButtonListener(it, youSearchedText)
            }
        )

        trackSearchAdapter = TrackAdapter(emptyList(), onTrackClickListener = {
            setOnTrackClickListenerLogic(it)
        })
        recyclerView.adapter = trackSearchAdapter

        val inputEditText = binding.inputEditText

        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) =
                Unit

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit

            override fun afterTextChanged(s: Editable?) {
                setTextWatcherLogic(s, inputEditText, youSearchedText)
            }
        }
        inputEditText.addTextChangedListener(simpleTextWatcher)

        updateButton = binding.buttonUpdate
        updateButton.setOnClickListener {
            searchTrack()
        }

        if (savedInstanceState != null) {
            text = savedInstanceState.getString(KEY_INPUT_TEXT).toString()
            inputEditText.setText(text)
            trackSearchAdapter.updateList {
                savedInstanceState.getParcelableArrayList(
                    KEY_SEARCH_TRACKLIST
                ) ?: mutableListOf()
            }

            searchActivityErrorState = SearchActivityError.valueOf(
                savedInstanceState.getString(KEY_SEARCH_ACTIVITY_ERROR_STATE).toString()
            )

            if (text.isNotEmpty()) {
                showPlaceholders(trackSearchAdapter.trackList)
            }
        }

        val imm: InputMethodManager =
            getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager

        binding.clearIcon.setOnClickListener {
            setClearIconOnClickListenerLogic(inputEditText, imm)
        }

        inputEditText.setOnFocusChangeListener { _, hasFocus ->
            setOnFocusChangeListenerLogic(hasFocus, inputEditText, youSearchedText)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(searchRunnable)
        useCaseRunnable?.let(handler::removeCallbacks)
        handler.removeCallbacks(isClickAllowedRunnable)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(KEY_INPUT_TEXT, text)
        outState.putParcelableArrayList(
            KEY_SEARCH_TRACKLIST,
            ArrayList(trackSearchAdapter.trackList)
        )
        outState.putString(KEY_SEARCH_ACTIVITY_ERROR_STATE, searchActivityErrorState.name)
    }

    private fun setTextPlaceholder(text: String) {
        if (text.isNotEmpty()) {
            textPlaceholder.isVisible = true
            trackSearchAdapter.clearList()
            textPlaceholder.text = text
        } else {
            textPlaceholder.isVisible = false
        }
    }

    private fun setImagePlaceholder(image: Int) {
        when (image) {
            android.R.color.transparent -> {
                imagePlaceholder.isVisible = false
                updateButton.isVisible = false
            }

            R.drawable.search_error_icon -> {
                imagePlaceholder.isVisible = true
                imagePlaceholder.setImageResource(image)
                updateButton.isVisible = true
            }

            else -> {
                imagePlaceholder.isVisible = true
                imagePlaceholder.setImageResource(image)
                updateButton.isVisible = false
            }
        }
    }

    private fun setupClearHistoryButtonListener(
        trackAdapter: TrackAdapter,
        youSearchedText: TextView,
    ) {
        trackAdapter.clearList()
        saveHistoryTrackUseCase.execute(trackAdapter.trackList)
        recyclerView.adapter = trackSearchAdapter
        youSearchedText.isVisible = false
        trackSearchAdapter.clearList()
    }

    private fun setOnFocusChangeListenerLogic(
        hasFocus: Boolean,
        inputEditText: EditText,
        youSearchedText: TextView,
    ) {
        val showHistory =
            hasFocus && inputEditText.text.isEmpty() && !trackHistoryAdapter.isEmpty()
        recyclerView.adapter = if (showHistory) trackHistoryAdapter else trackSearchAdapter
        youSearchedText.isVisible = showHistory
    }

    private fun setOnTrackClickListenerLogic(track: TrackInfo) {
        if (recyclerView.adapter == trackSearchAdapter) {
            trackHistoryAdapter.updateList {
                val historyList = addToHistoryList(track, it)
                saveHistoryTrackUseCase.execute(historyList)
                historyList
            }
        }
        if (clickDebounce()) {
            val audioplayerIntent = Intent(this, AudioplayerActivity::class.java)
            audioplayerIntent.putExtra(KEY_SELECTED_TRACK_DETAILS, track)
            startActivity(audioplayerIntent)
        }
    }

    private fun addToHistoryList(
        track: TrackInfo,
        historyTrackList: List<TrackInfo>,
    ): List<TrackInfo> {
        val filteredList = historyTrackList - track
        val updatedList = listOf(track) + filteredList
        return if (updatedList.size > 10) updatedList.subList(0, 10) else updatedList
    }

    private fun setClearIconOnClickListenerLogic(inputEditText: EditText, imm: InputMethodManager) {
        handler.removeCallbacks(searchRunnable)
        handler.removeCallbacks(isClickAllowedRunnable)

        inputEditText.setText("")
        imm.hideSoftInputFromWindow(inputEditText.windowToken, 0)
        if (trackSearchAdapter.trackList.isNotEmpty()) {
            trackSearchAdapter.clearList()
        }
    }

    private fun setTextWatcherLogic(
        s: Editable?,
        inputEditText: EditText,
        youSearchedText: TextView,
    ) {
        binding.clearIcon.isVisible = !s.isNullOrEmpty()
        text = inputEditText.text.toString()

        val showHistory =
            inputEditText.hasFocus() && s?.isEmpty() == true && !trackHistoryAdapter.isEmpty()

        recyclerView.adapter = if (showHistory) trackHistoryAdapter else trackSearchAdapter
        youSearchedText.isVisible = showHistory

        if (recyclerView.adapter == trackHistoryAdapter) {
            imagePlaceholder.isVisible = false
            textPlaceholder.isVisible = false
            updateButton.isVisible = false
        }
        if (s?.isNotBlank() == true) {
            searchDebounce()
        } else {
            handler.removeCallbacks(searchRunnable)
            trackSearchAdapter.clearList()
        }
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed(isClickAllowedRunnable, TRACK_CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    private fun searchDebounce() {
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
    }

    private fun searchTrack() {
        searchTrackUseCase.execute(
            trackName = text,
            consumer = object : Consumer<ConsumerData<List<TrackInfo>>> {
                override fun accept(data: ConsumerData<List<TrackInfo>>) {
                    searchActivityErrorState = SearchActivityError.NO_ERROR
                    val currentRunnable = useCaseRunnable
                    currentRunnable?.let { handler.removeCallbacks(currentRunnable) }

                    val newTrackInfoRunnable = Runnable {
                        when (data) {
                            is ConsumerData.Error -> {
                                searchActivityErrorState = SearchActivityError.SEARCH_ERROR
                            }

                            is ConsumerData.InternetConnectionError -> {
                                searchActivityErrorState = SearchActivityError.NETWORK_ERROR
                            }

                            is ConsumerData.Data -> {
                                searchActivityErrorState = SearchActivityError.NO_ERROR
                                val trackListFound = data.value
                                trackSearchAdapter.updateList { trackListFound }
                                recyclerView.isVisible = true
                            }
                        }

                        showPlaceholders(trackSearchAdapter.trackList)
                    }

                    useCaseRunnable = newTrackInfoRunnable
                    handler.post(newTrackInfoRunnable)
                }
            }
        )
    }

    private fun showPlaceholders(currentSearchAdapterTrackList: List<TrackInfo>) {
        when (searchActivityErrorState) {
            SearchActivityError.SEARCH_ERROR -> {
                setTextPlaceholder(getString(R.string.server_error))
                setImagePlaceholder(R.drawable.search_error_icon)
            }

            SearchActivityError.NETWORK_ERROR -> {
                setTextPlaceholder(getString(R.string.search_error_message))
                setImagePlaceholder(R.drawable.search_error_icon)
            }

            SearchActivityError.NO_ERROR -> {
                if (currentSearchAdapterTrackList.isNotEmpty()) {
                    setTextPlaceholder("")
                    setImagePlaceholder(android.R.color.transparent)
                } else {
                    setTextPlaceholder(getString(R.string.nothing_found))
                    setImagePlaceholder(R.drawable.not_found_icon)
                }
            }
        }
    }

    enum class SearchActivityError {
        NETWORK_ERROR,
        SEARCH_ERROR,
        NO_ERROR
    }
}