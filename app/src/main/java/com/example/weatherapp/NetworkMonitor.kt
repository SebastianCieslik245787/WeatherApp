package com.example.weatherapp

import android.annotation.SuppressLint
import android.content.Context
import android.content.Context.CONNECTIVITY_SERVICE
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkRequest
import androidx.core.content.ContextCompat.getSystemService

class NetworkMonitor(private val context: Context) {

    private var isConnected = false

    var onInternetAvailable: (() -> Unit)? = null
    var onInternetLost: (() -> Unit)? = null

    @SuppressLint("ServiceCast")
    fun startMonitoring() {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val builder = NetworkRequest.Builder()
        connectivityManager.registerNetworkCallback(
            builder.build(),
            object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    isConnected = true
                    onInternetAvailable?.invoke()
                }

                override fun onLost(network: Network) {
                    isConnected = false
                    onInternetLost?.invoke()
                }
            }
        )
    }

    fun isConnected(): Boolean {
        return isConnected
    }
}
