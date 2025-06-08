package com.example.weatherapp

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import org.json.JSONObject
import java.time.Instant

class FutureWeatherForecast {
    private lateinit var forecastItems: MutableList<FutureWeatherForecastItem>

    @SuppressLint("SuspiciousIndentation")
    fun initialize(context: Context, city: City): Boolean {
        val hourlyWeatherForecastJSONString = loadActiveCityForecastFromJSON(city.getFileName(), context)
        val hourlyWeatherForecastJSONObject = JSONObject(hourlyWeatherForecastJSONString)

        val data = hourlyWeatherForecastJSONObject.getJSONArray("list")

        if (data.length() < 1) return false

        forecastItems = mutableListOf()

        val actualTime = Instant.now().epochSecond

        for (i in 0 until data.length()) {

            var tempTimeStamp = actualTime - data.getJSONObject(i).get("dt").toString().toLong()

            if(tempTimeStamp < 0 ) forecastItems.add(FutureWeatherForecastItem(data.get(i) as JSONObject))
        }
        return true
    }

    fun loadActiveCityForecastFromJSON(fileName: String, context: Context): String {
        return context.openFileInput(fileName).bufferedReader().use { it.readText() }
    }

    fun getForecast(): MutableList<FutureWeatherForecastItem> {
        return forecastItems
    }
}