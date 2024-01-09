package com.practicum.playlistmaker

import android.graphics.drawable.BitmapDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.databinding.ActivitySearchBinding
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private const val ITUNES_URL = "https://itunes.apple.com"

class SearchActivity : AppCompatActivity() {

    private var text: String = ""
    private val retrofit = Retrofit.Builder()
        .baseUrl(ITUNES_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val iTunesApi = retrofit.create(ItunesApi::class.java)
    private val trackSearchAdapter = TrackSearchAdapter()
    private lateinit var binding: ActivitySearchBinding
    private lateinit var updateButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val inputEditText = binding.inputEditText

        binding.toolbarSearch.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        trackSearchAdapter.trackList = listOf<Track>()
        val searchRecyclerView = binding.searchRecyclerView
        searchRecyclerView.layoutManager =
            LinearLayoutManager(this@SearchActivity, LinearLayoutManager.VERTICAL, false)
        searchRecyclerView.adapter = trackSearchAdapter

        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) =
                Unit

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit

            override fun afterTextChanged(s: Editable?) {
                binding.clearIcon.isVisible = !s.isNullOrEmpty()
                text = inputEditText.text.toString()
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
                    val trackResponse = iTunesApi.searchTrack(text)
                    if (trackResponse.results.isNotEmpty()) {
                        trackSearchAdapter.trackList = trackResponse.results
                        trackSearchAdapter.notifyDataSetChanged()
                        setTextPlaceholder("")
                        setImagePlaceholder(android.R.color.transparent)
                    } else {
                        setTextPlaceholder(getString(R.string.nothing_found))
                        setImagePlaceholder(R.drawable.not_found_icon)
                    }
                } catch (e: Exception) {
                    setTextPlaceholder(getString(R.string.search_error_message))
                    setImagePlaceholder(R.drawable.search_error_icon)
                }
                this@launch.cancel()
            }
        }
    }

    companion object {
        private const val KEY_INPUT_TEXT = "INPUT_TEXT"
    }
}
