package com.practicum.playlistmaker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import com.practicum.playlistmaker.databinding.ActivitySearchBinding

class SearchActivity : AppCompatActivity() {

    private var text: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val inputEditText = binding.inputEditText

        if (savedInstanceState != null) {
            text = savedInstanceState.getString(INPUT_TEXT).toString()
            inputEditText.setText(text)
        }

        binding.toolbarSearch.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        val imm: InputMethodManager =
            getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager

        binding.clearIcon.setOnClickListener {
            inputEditText.setText("")

            imm.hideSoftInputFromWindow(inputEditText.windowToken, 0)
        }

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
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(INPUT_TEXT, text)
    }

    companion object {
        private const val INPUT_TEXT = "INPUT_TEXT"
    }
}