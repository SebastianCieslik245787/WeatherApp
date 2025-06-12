package com.example.weatherapp

import android.net.ConnectivityManager
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity(), IFragmentLoadListener {
    private var buttonForecast: ImageButton? = null
    private var buttonFindCity: ImageButton? = null
    private var buttonSettings: ImageButton? = null
    private lateinit var loadingSpinner: ProgressBar
    private lateinit var error: TextView
    private var fragmentFrame: FrameLayout? = null

    private lateinit var networkMonitor: NetworkMonitor

    private var REFRESH_TIMESTAMP: Long = 1000 * 60 * 15

    override fun onStart() {
        super.onStart()
        if (!isCurrentlyConnected()) {
            toggleError(0.82f, true)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setup()

        lifecycleScope.launch {
            APIController.refreshActiveCity(this@MainActivity)
        }

        lifecycleScope.launch {
            while (isActive) {
                APIController.refreshAllFavouriteCities(this@MainActivity)
                delay(REFRESH_TIMESTAMP)
            }
        }

        if (findViewById<FrameLayout?>(R.id.forecastContainer) != null && findViewById<FrameLayout?>(
                R.id.findCityContainer
            ) != null && findViewById<FrameLayout?>(R.id.settingsContainer) != null
        ) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.forecastContainer, WeatherFragment())
                .replace(R.id.findCityContainer, FindCityFragment())
                .replace(R.id.settingsContainer, SettingsFragment())
                .commit()
        } else {
            onFragmentLoading()
            replaceFragment(WeatherFragment())
        }
    }

    private fun setup() {
        buttonForecast = findViewById(R.id.buttonForecast)
        buttonFindCity = findViewById(R.id.buttonFind)
        buttonSettings = findViewById(R.id.buttonSettings)
        loadingSpinner = findViewById(R.id.loadingSpinner)
        error = findViewById(R.id.error)
        fragmentFrame = findViewById(R.id.fragmentContainer)

        networkMonitor = NetworkMonitor(this)
        networkMonitor.startMonitoring()

        networkMonitor.onInternetLost = {
            runOnUiThread {
                toggleError(0.82f, true)
            }
        }

        networkMonitor.onInternetAvailable = {
            runOnUiThread {
                toggleError(0.92f, false)
            }
        }

        buttonForecast?.setOnClickListener { replaceFragment(WeatherFragment()) }
        buttonFindCity?.setOnClickListener { replaceFragment(FindCityFragment()) }

        buttonSettings?.setOnClickListener { replaceFragment(SettingsFragment()) }
    }

    override fun onFragmentLoaded() {
        loadingSpinner.visibility = View.GONE
        findViewById<View>(R.id.fragmentContainer).visibility = View.VISIBLE
    }

    override fun onFragmentLoading() {
        loadingSpinner.visibility = View.VISIBLE
        findViewById<View>(R.id.fragmentContainer).visibility = View.GONE
    }

    private fun replaceFragment(fragment: Fragment) {
        onFragmentLoading()

        if (fragment is IFragment) {
            fragment.setLoadListener(this)
        }

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .addToBackStack(null)
            .commit()
    }

    private fun isCurrentlyConnected(): Boolean {
        val connectivityManager = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetworkInfo
        return activeNetwork != null && activeNetwork.isConnected
    }

    private fun toggleError(value: Float, errorVisible: Boolean) {
        if (errorVisible) error.visibility = View.VISIBLE
        else error.visibility = View.GONE
        if(fragmentFrame != null){
            val params = fragmentFrame!!.layoutParams as ConstraintLayout.LayoutParams
            params.matchConstraintPercentHeight = value
            fragmentFrame!!.layoutParams = params
        }
    }
}