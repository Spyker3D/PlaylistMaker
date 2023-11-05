package com.practicum.playlistmaker

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val searchButton = findViewById<Button>(R.id.searchButton)
        val mediaLibraryButton = findViewById<Button>(R.id.mediaLibraryButtton)
        val settingsButton = findViewById<Button>(R.id.settingsButton)

        val searchButtonListener: View.OnClickListener = object : View.OnClickListener {
            override fun onClick(v: View?) {
                Toast.makeText(this@MainActivity, "Вы нажали на кнопку 'Поиск'", Toast.LENGTH_SHORT).show()
            }
        }
        searchButton.setOnClickListener(searchButtonListener)

        mediaLibraryButton.setOnClickListener {
            Toast.makeText(this@MainActivity, "Вы нажали на кнопку 'Медиатека'", Toast.LENGTH_SHORT).show()
        }

        settingsButton.setOnClickListener {
            //Toast.makeText(this@MainActivity, "Вы нажали на кнопку 'Настройки'", Toast.LENGTH_SHORT).show()
            val settingsIntent = Intent(this, SettingsActivity::class.java)
            startActivity(settingsIntent)
        }
    }
}