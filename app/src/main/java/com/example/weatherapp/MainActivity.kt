package com.example.weatherapp

import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

class MainActivity : AppCompatActivity(), IFragmentLoadListener {
    private lateinit var buttonForecast: ImageButton
    private lateinit var buttonFindCity: ImageButton
    private lateinit var buttonSettings: ImageButton
    private lateinit var loadingSpinner: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setup()

        replaceFragment(WeatherFragment())
    }

    private fun setup() {
        buttonForecast = findViewById(R.id.buttonForecast)
        buttonFindCity = findViewById(R.id.buttonFind)
        buttonSettings = findViewById(R.id.buttonSettings)
        loadingSpinner = findViewById(R.id.loadingSpinner)

        buttonForecast.setOnClickListener { replaceFragment(WeatherFragment()) }

        buttonSettings.setOnClickListener { replaceFragment(SettingsFragment()) }
    }

    override fun onFragmentLoaded() {
        loadingSpinner.visibility = View.GONE
        findViewById<View>(R.id.fragmentContainer).visibility = View.VISIBLE
    }

    private fun replaceFragment(fragment: Fragment) {
        loadingSpinner.visibility = View.VISIBLE
        findViewById<View>(R.id.fragmentContainer).visibility = View.GONE

        if (fragment is IFragment) {
            fragment.setLoadListener(this)
        }

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .addToBackStack(null)
            .commit()
    }
}