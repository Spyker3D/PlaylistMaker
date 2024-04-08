package com.practicum.playlistmaker.search.ui.activity

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
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ActivitySearchBinding
import com.practicum.playlistmaker.search.domain.entities.TrackInfo
import com.practicum.playlistmaker.player.ui.activity.AudioplayerActivity
import com.practicum.playlistmaker.player.ui.activity.KEY_SELECTED_TRACK_DETAILS
import com.practicum.playlistmaker.search.ui.entities.SearchState
import com.practicum.playlistmaker.search.ui.viewModel.TrackSearchViewModel

private const val KEY_INPUT_TEXT = "INPUT_TEXT"
private const val KEY_SEARCH_TRACKLIST = "TRACKLIST_SEARCH"
private const val TRACK_CLICK_DEBOUNCE_DELAY = 1000L

class SearchActivity : AppCompatActivity() {

    private var text: String = ""
    private var isClickAllowed = true

    private val handler = Handler(Looper.getMainLooper())
    private val isClickAllowedRunnable = Runnable { isClickAllowed = true }

    private lateinit var trackSearchAdapter: TrackAdapter
    private lateinit var trackHistoryAdapter: TrackAdapter
    private lateinit var binding: ActivitySearchBinding
    private lateinit var updateButton: Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var imagePlaceholder: ImageView
    private lateinit var textPlaceholder: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var youSearchedText: TextView

    private lateinit var viewModel: TrackSearchViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbarSearch.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        imagePlaceholder = binding.imagePlaceholder
        textPlaceholder = binding.textPlaceholder
        progressBar = binding.progressCircular

        recyclerView = binding.searchRecyclerView
        recyclerView.layoutManager =
            LinearLayoutManager(this@SearchActivity, LinearLayoutManager.VERTICAL, false)

        youSearchedText = binding.youSearchedText

        viewModel = ViewModelProvider(
            this,
            TrackSearchViewModel.getViewModelFactory()
        )[TrackSearchViewModel::class.java]

        trackHistoryAdapter = TrackAdapter(
            trackList = viewModel.getHistoryList(),
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
                binding.clearIcon.isVisible = !s.isNullOrEmpty()
                text = inputEditText.text.toString()  // нужно ли?

                val showHistory =
                    inputEditText.hasFocus() && s?.isEmpty() == true && !trackHistoryAdapter.isEmpty()

                recyclerView.adapter = if (showHistory) trackHistoryAdapter else trackSearchAdapter
                if (showHistory) viewModel.showHistoryList()
                youSearchedText.isVisible = showHistory

                if (s?.isNotBlank() == true) {
                    viewModel.searchDebounce(changedText = s.toString(), false)
                } else {
                    viewModel.removeCallbacks()
                    trackSearchAdapter.clearList()
                }
            }
        }
        inputEditText.addTextChangedListener(simpleTextWatcher)

        viewModel.observeState().observe(this) { render(it) }

        updateButton = binding.buttonUpdate
        updateButton.setOnClickListener {
            viewModel.searchDebounce(
                changedText = inputEditText.text.toString(),
                forceSearch = true
            )
        }

        if (savedInstanceState != null) {
            text = savedInstanceState.getString(KEY_INPUT_TEXT).toString()
            inputEditText.setText(text)
            trackSearchAdapter.updateList {
                savedInstanceState.getParcelableArrayList(
                    KEY_SEARCH_TRACKLIST
                ) ?: mutableListOf()
            }
        }

        val imm: InputMethodManager =
            getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager

        binding.clearIcon.setOnClickListener {
            setClearIconOnClickListenerLogic(inputEditText, imm)
        }

        inputEditText.setOnFocusChangeListener { _, hasFocus ->
            setOnFocusChangeListenerLogic(
                hasFocus,
                inputEditText,
                youSearchedText
            )
        }
    }

    private fun render(state: SearchState) {
        when (state) {
            is SearchState.Loading -> showLoading()
            is SearchState.Content -> showContent(state.trackList)
            is SearchState.Error -> showError(state.errorMessage)
            is SearchState.Empty -> showEmpty(state.message)
            is SearchState.HistoryListPresentation -> showContent(state.historyTrackList)
        }
    }

    private fun showLoading() {
        textPlaceholder.isVisible = false
        imagePlaceholder.isVisible = false
        updateButton.isVisible = false
        recyclerView.isVisible = false
        progressBar.isVisible = true
        youSearchedText.isVisible = false
    }

    private fun showError(errorMessage: String) {
        progressBar.isVisible = false
        textPlaceholder.isVisible = true
        textPlaceholder.text = errorMessage
        imagePlaceholder.isVisible = true
        setImagePlaceholder(R.drawable.search_error_icon)
        updateButton.isVisible = false
        recyclerView.isVisible = false
        youSearchedText.isVisible = false
    }

    private fun showEmpty(emptyMessage: String) {
        progressBar.isVisible = false
        textPlaceholder.isVisible = true
        textPlaceholder.text = emptyMessage
        imagePlaceholder.isVisible = true
        setImagePlaceholder(R.drawable.not_found_icon)
        updateButton.isVisible = false
        recyclerView.isVisible = false
        youSearchedText.isVisible = false
    }

    private fun showContent(trackList: List<TrackInfo>) {
        progressBar.isVisible = false
        textPlaceholder.isVisible = false
        imagePlaceholder.isVisible = false
        updateButton.isVisible = false
        recyclerView.isVisible = true
        if (recyclerView.adapter == trackSearchAdapter) {
            trackSearchAdapter.updateList { trackList }
        } else {
            trackHistoryAdapter.updateList { trackList }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(isClickAllowedRunnable)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(KEY_INPUT_TEXT, text)
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
        viewModel.clearHistoryList()
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
            viewModel.addTrackToHistoryList(track)
            trackHistoryAdapter.updateList { viewModel.getHistoryList() }
        }
        if (clickDebounce()) {
            viewModel.saveSelectedTrack(track)
            val audioplayerIntent = Intent(this, AudioplayerActivity::class.java)
            audioplayerIntent.putExtra(KEY_SELECTED_TRACK_DETAILS, track)
            startActivity(audioplayerIntent)
        }
    }

    private fun setClearIconOnClickListenerLogic(inputEditText: EditText, imm: InputMethodManager) {
        handler.removeCallbacks(isClickAllowedRunnable)

        inputEditText.setText("")
        imm.hideSoftInputFromWindow(inputEditText.windowToken, 0)
        if (trackSearchAdapter.trackList.isNotEmpty()) {
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
}