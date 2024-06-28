package com.practicum.playlistmaker.utils

import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.practicum.playlistmaker.R

fun Fragment.showSnackBar(message: String) {
    Snackbar
        .make(requireView(), message, Snackbar.LENGTH_SHORT)
        .setBackgroundTint(resources.getColor(R.color.snack_bar_color))
        .show()

}