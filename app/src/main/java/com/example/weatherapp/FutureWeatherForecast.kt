package com.example.weatherapp

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import org.json.JSONObject
import java.time.Instant

class FutureWeatherForecast {
    private lateinit var forecastItems: MutableList<FutureWeatherForecastItem>
    private val DAY_IN_SECONDS: Int = 86400

    @SuppressLint("SuspiciousIndentation")
    fun initialize(context: Context): Boolean {
        val hourlyWeatherForecastJSONString = loadJSONFromAsset("4dayforecast_EBE.json", context)
        val hourlyWeatherForecastJSONObject = JSONObject(hourlyWeatherForecastJSONString)

        val data = hourlyWeatherForecastJSONObject.getJSONArray("list")

        if (data.length() < 1) return false

        forecastItems = mutableListOf()

        val actualTime = Instant.now().epochSecond

        var timeStamp = DAY_IN_SECONDS - (actualTime % DAY_IN_SECONDS) + DAY_IN_SECONDS
        Log.d("XD7", timeStamp.toString())

        for (i in 0 until data.length()) {
            Log.d(
                "XD2",
                "${
                    actualTime - data.getJSONObject(i).get("dt").toString().toLong()
                } | ${data.getJSONObject(i).get("dt").toString().toLong()} | $actualTime"
            )

            var tempTimeStamp = actualTime - data.getJSONObject(i).get("dt").toString().toLong()

            //if(tempTimeStamp > 0 && tempTimeStamp <= timeStamp)
            forecastItems.add(FutureWeatherForecastItem(data.get(i) as JSONObject))
        }
        return true
    }

    fun loadJSONFromAsset(fileName: String, context: Context): String {
        return context.assets.open(fileName).bufferedReader().use { it.readText() }
    }

    fun getForecast(): MutableList<FutureWeatherForecastItem> {
        Log.d("XD5", forecastItems.size.toString())
        return forecastItems
    }
}