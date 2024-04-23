package com.practicum.playlistmaker.search.presentation.ui

import android.content.Context.INPUT_METHOD_SERVICE
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
import androidx.recyclerview.widget.LinearLayoutManager
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentSearchBinding
import com.practicum.playlistmaker.player.presentation.AudioplayerActivity
import com.practicum.playlistmaker.player.presentation.KEY_SELECTED_TRACK_DETAILS
import com.practicum.playlistmaker.search.presentation.entities.SearchState
import com.practicum.playlistmaker.search.presentation.entities.Track
import com.practicum.playlistmaker.search.presentation.viewmodel.TrackSearchViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

private const val KEY_INPUT_TEXT = "INPUT_TEXT"
private const val TRACK_CLICK_DEBOUNCE_DELAY = 1000L

