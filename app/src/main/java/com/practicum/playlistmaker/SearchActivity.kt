package com.practicum.playlistmaker

import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.practicum.playlistmaker.databinding.ActivitySearchBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException

private const val ITUNES_URL = "https://itunes.apple.com"
private const val KEY_INPUT_TEXT = "INPUT_TEXT"

class SearchActivity : AppCompatActivity() {

    private var text: String = ""
    private val retrofit = Retrofit.Builder()
        .baseUrl(ITUNES_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val iTunesApi = retrofit.create(ItunesApi::class.java)
    private lateinit var trackSearchAdapter: TrackAdapter
    private lateinit var binding: ActivitySearchBinding
    private lateinit var updateButton: Button
    private lateinit var sharedPrefsListener: OnSharedPreferenceChangeListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sharedPreferences = getSharedPreferences(
            PLAYLISTMAKER_SHARED_PREFS,
            MODE_PRIVATE
        )

        binding.toolbarSearch.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        val recyclerView = binding.searchRecyclerView
        recyclerView.layoutManager =
            LinearLayoutManager(this@SearchActivity, LinearLayoutManager.VERTICAL, false)

        val youSearchedText: TextView = binding.youSearchedText

        val trackHistoryAdapter = TrackAdapter(
            readFromTrackListHistoryFromSharedPrefs(sharedPreferences).toMutableList(),
            onActionButtonClickListener = {
                it.clearList()
                writeTrackListHistoryToSharedPrefs(
                    sharedPreferences, it.trackList
                )
                recyclerView.adapter = trackSearchAdapter
                youSearchedText.isVisible = false
                trackSearchAdapter.clearList()
            }
        )

        val onTrackClickListener = OnTrackClickListener { track ->
            trackHistoryAdapter.updateList {
                val historyList = addToHistoryList(track, it) // it = adapter.trackList
                writeTrackListHistoryToSharedPrefs(sharedPreferences, historyList)
                historyList
            }
        }

        trackSearchAdapter = TrackAdapter(emptyList(), onTrackClickListener)
        recyclerView.adapter = trackSearchAdapter

        val inputEditText = binding.inputEditText

        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) =
                Unit

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit

            override fun afterTextChanged(s: Editable?) {
                binding.clearIcon.isVisible = !s.isNullOrEmpty()
                text = inputEditText.text.toString()

                val showHistory =
                    inputEditText.hasFocus() && s?.isEmpty() == true && !trackHistoryAdapter.isEmpty()

                recyclerView.adapter = if (showHistory) trackHistoryAdapter else trackSearchAdapter
                youSearchedText.isVisible = showHistory
            }
        }
        inputEditText.addTextChangedListener(simpleTextWatcher)
        inputEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                search()
            }
            false
        }

        if (savedInstanceState != null) {
            text = savedInstanceState.getString(KEY_INPUT_TEXT).toString()
            inputEditText.setText(text)
        }

        val imm: InputMethodManager =
            getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager

        binding.clearIcon.setOnClickListener {
            inputEditText.setText("")
            imm.hideSoftInputFromWindow(inputEditText.windowToken, 0)
            trackSearchAdapter.clearList()
        }

        updateButton = binding.buttonUpdate
        updateButton.setOnClickListener {
            search()
        }

        sharedPrefsListener = OnSharedPreferenceChangeListener { sharedPreferences, key ->
            historyAdapterRestore(sharedPreferences, key, trackHistoryAdapter)
        }

        inputEditText.setOnFocusChangeListener { _, hasFocus ->
            val showHistory =
                hasFocus && inputEditText.text.isEmpty() && !trackHistoryAdapter.isEmpty()
            recyclerView.adapter = if (showHistory) trackHistoryAdapter else trackSearchAdapter
            youSearchedText.isVisible = showHistory
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(KEY_INPUT_TEXT, text)
    }

    private fun setTextPlaceholder(text: String) {
        val textPlaceholder = binding.textPlaceholder
        if (text.isNotEmpty()) {
            textPlaceholder.isVisible = true
            trackSearchAdapter.clearList()
            textPlaceholder.text = text
        } else {
            textPlaceholder.isVisible = false
        }
    }

    private fun setImagePlaceholder(image: Int) {
        val imagePlaceholder = binding.imagePlaceholder
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

    private fun search() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                try {
                    val trackResponse = withContext(Dispatchers.IO) {
                        iTunesApi.searchTrack(text).execute()
                    }
                    if (trackResponse.code() == 200) {
                        if (trackResponse.body()?.results?.isNotEmpty() == true) {
                            trackSearchAdapter.updateList { trackResponse.body()?.results!! }
                            setTextPlaceholder("")
                            setImagePlaceholder(android.R.color.transparent)
                        } else {
                            setTextPlaceholder(getString(R.string.nothing_found))
                            setImagePlaceholder(R.drawable.not_found_icon)
                        }
                    } else {
                        setTextPlaceholder(getString(R.string.server_error))
                        setImagePlaceholder(R.drawable.search_error_icon)
                    }
                } catch (e: IOException) {
                    Log.e("SearchActivity", "Network error", e)
                    setTextPlaceholder(getString(R.string.search_error_message))
                    setImagePlaceholder(R.drawable.search_error_icon)
                } finally {
                    this@launch.cancel()
                }
            }
        }
    }
}
