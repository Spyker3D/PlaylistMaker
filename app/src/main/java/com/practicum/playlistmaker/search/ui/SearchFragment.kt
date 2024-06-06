package com.practicum.playlistmaker.search.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentSearchBinding
import com.practicum.playlistmaker.player.presentation.AudioplayerActivity
import com.practicum.playlistmaker.player.presentation.KEY_SELECTED_TRACK_DETAILS
import com.practicum.playlistmaker.search.presentation.entities.SearchState
import com.practicum.playlistmaker.search.presentation.entities.Track
import com.practicum.playlistmaker.search.presentation.viewmodel.TrackSearchViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

private const val KEY_INPUT_TEXT = "INPUT_TEXT"
private const val TRACK_CLICK_DEBOUNCE_DELAY = 200L

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private var isClickAllowed = true

    private lateinit var trackSearchAdapter: TrackAdapter
    private lateinit var trackHistoryAdapter: TrackAdapter
    private val viewModel: TrackSearchViewModel by viewModel<TrackSearchViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.searchRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        trackHistoryAdapter = TrackAdapter(
            trackList = emptyList(), // historyList,
            onTrackClickListener = { addToHistoryAndSwitchToAudioPlayer(it) },
            onActionButtonClickListener = {
                setupClearHistoryButtonListener(it, binding.youSearchedText)
            }
        )

        trackSearchAdapter = TrackAdapter(emptyList(), onTrackClickListener = {
            addToHistoryAndSwitchToAudioPlayer(it)
        })
        binding.searchRecyclerView.adapter = trackSearchAdapter

        val inputEditText = binding.inputEditText

        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) =
                Unit

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit

            override fun afterTextChanged(s: Editable?) {
                binding.clearIcon.isVisible = !s.isNullOrEmpty()
                viewModel.onTextChanged(s?.toString(), binding.inputEditText.hasFocus())
            }
        }
        inputEditText.addTextChangedListener(simpleTextWatcher)

        viewModel.stateLiveData.observe(viewLifecycleOwner) { render(it) }

        binding.buttonUpdate.setOnClickListener {
            viewModel.searchDebounce(
                changedText = inputEditText.text.toString(),
                forceSearch = true
            )
        }

        if (savedInstanceState != null) {
            inputEditText.setText(savedInstanceState.getString(KEY_INPUT_TEXT))
        }

        val imm: InputMethodManager =
            requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        binding.clearIcon.setOnClickListener {
            setClearIconOnClickListenerLogic(inputEditText, imm)
        }

        inputEditText.setOnFocusChangeListener { _, hasFocus ->
            viewModel.onTextChanged(binding.inputEditText.text.toString(), hasFocus)
        }
    }

    private fun render(state: SearchState) {
        binding.searchRecyclerView.adapter = when (state) {
            is SearchState.Content -> trackSearchAdapter
            is SearchState.HistoryListPresentation -> trackHistoryAdapter.apply {
                updateList { state.historyTrackList }
            }

            else -> null
        }
        when (state) {
            is SearchState.Loading -> showLoading()
            is SearchState.Content -> showContent(state.trackList)
            is SearchState.Error -> showError(state, state.errorMessage)
            is SearchState.InternetConnectionError -> showError(state, state.errorMessage)
            is SearchState.Empty -> showEmpty(state.message)
            is SearchState.HistoryListPresentation -> showContent(state.historyTrackList)
        }
    }

    private fun showLoading() {
        binding.textPlaceholder.isVisible = false
        binding.imagePlaceholder.isVisible = false
        binding.buttonUpdate.isVisible = false
        binding.searchRecyclerView.isVisible = false
        binding.progressCircular.isVisible = true
        binding.youSearchedText.isVisible = false
    }

    private fun showError(state: SearchState, errorMessage: String) {
        binding.progressCircular.isVisible = false
        binding.textPlaceholder.isVisible = true
        binding.textPlaceholder.text = errorMessage
        binding.imagePlaceholder.isVisible = true
        setImagePlaceholder(R.drawable.search_error_icon)
        binding.buttonUpdate.isVisible = state is SearchState.InternetConnectionError
        binding.searchRecyclerView.isVisible = false
        binding.youSearchedText.isVisible = false
    }

    private fun showEmpty(emptyMessage: String) {
        binding.progressCircular.isVisible = false
        binding.textPlaceholder.isVisible = true
        binding.textPlaceholder.text = emptyMessage
        binding.imagePlaceholder.isVisible = true
        setImagePlaceholder(R.drawable.not_found_icon)
        binding.buttonUpdate.isVisible = false
        binding.searchRecyclerView.isVisible = false
        binding.youSearchedText.isVisible = false
    }

    private fun showContent(trackList: List<Track>) {
        binding.progressCircular.isVisible = false
        binding.textPlaceholder.isVisible = false
        binding.imagePlaceholder.isVisible = false
        binding.buttonUpdate.isVisible = false
        binding.searchRecyclerView.isVisible = true
        if (binding.searchRecyclerView.adapter == trackSearchAdapter) {
            trackSearchAdapter.updateList { trackList }
        } else {
            if (trackList.isNotEmpty() && binding.inputEditText.hasFocus()) {
                binding.youSearchedText.isVisible = true
                trackHistoryAdapter.updateList { trackList }
            } else {
                binding.youSearchedText.isVisible = false
                binding.searchRecyclerView.isVisible = false
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(KEY_INPUT_TEXT, binding.inputEditText.text?.toString())
    }

    private fun setImagePlaceholder(image: Int) {
        when (image) {
            android.R.color.transparent -> {
                binding.imagePlaceholder.isVisible = false
                binding.buttonUpdate.isVisible = false
            }

            R.drawable.search_error_icon -> {
                binding.imagePlaceholder.isVisible = true
                binding.imagePlaceholder.setImageResource(image)
                binding.buttonUpdate.isVisible = true
            }

            else -> {
                binding.imagePlaceholder.isVisible = true
                binding.imagePlaceholder.setImageResource(image)
                binding.buttonUpdate.isVisible = false
            }
        }
    }

    private fun setupClearHistoryButtonListener(
        trackAdapter: TrackAdapter,
        youSearchedText: TextView,
    ) {
        trackAdapter.clearList()
        viewModel.clearHistoryList()
        binding.searchRecyclerView.adapter = trackSearchAdapter
        youSearchedText.isVisible = false
        trackSearchAdapter.clearList()
    }

    private fun addToHistoryAndSwitchToAudioPlayer(track: Track) {
        if (binding.searchRecyclerView.adapter == trackSearchAdapter) {
            viewModel.addTrackToHistoryList(track)
        }
        if (clickDebounce()) {
            val audioplayerIntent = Intent(requireContext(), AudioplayerActivity::class.java)
            audioplayerIntent.putExtra(KEY_SELECTED_TRACK_DETAILS, track)
            startActivity(audioplayerIntent)
        }
    }

    private fun setClearIconOnClickListenerLogic(inputEditText: EditText, imm: InputMethodManager) {
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
            viewLifecycleOwner.lifecycleScope.launch {
                delay(TRACK_CLICK_DEBOUNCE_DELAY)
                isClickAllowed = true
            }
        }
        return current
    }
}