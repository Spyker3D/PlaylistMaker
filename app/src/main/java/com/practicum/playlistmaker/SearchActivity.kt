package com.practicum.playlistmaker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.InputMethodManager
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.practicum.playlistmaker.databinding.ActivitySearchBinding

class SearchActivity : AppCompatActivity() {

    private var text: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val inputEditText = binding.inputEditText

        if (savedInstanceState != null) {
            text = savedInstanceState.getString(KEY_INPUT_TEXT).toString()
            inputEditText.setText(text)
        }

        binding.toolbarSearch.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        val imm: InputMethodManager =
            getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager

        lateinit var trackSearchAdapter: TrackSearchAdapter

        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) =
                Unit

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit

            override fun afterTextChanged(s: Editable?) {
                binding.clearIcon.isVisible = !s.isNullOrEmpty()
                text = inputEditText.text.toString()

                val searchRecyclerView = binding.searchRecyclerView
                searchRecyclerView.layoutManager =
                    LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)

                val filteredTrackList =
                    trackList.filter {
                        it.trackName.contains(
                            text,
                            ignoreCase = true
                        ) or it.artistName.contains(text, ignoreCase = true)
                    }

                trackSearchAdapter = TrackSearchAdapter(filteredTrackList)
                searchRecyclerView.adapter = trackSearchAdapter
            }
        }
        inputEditText.addTextChangedListener(simpleTextWatcher)

        binding.clearIcon.setOnClickListener {
            inputEditText.setText("")
            imm.hideSoftInputFromWindow(inputEditText.windowToken, 0)
            trackSearchAdapter.clearList()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(KEY_INPUT_TEXT, text)
    }

    companion object {
        private const val KEY_INPUT_TEXT = "INPUT_TEXT"
    }
}