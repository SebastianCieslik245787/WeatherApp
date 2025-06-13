package com.example.weatherapp

import android.annotation.SuppressLint
import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import java.time.Instant

class FutureWeatherForecast {
    private lateinit var forecastItems: MutableList<FutureWeatherForecastItem>
    private var UNIX_HOUR : Long = 3600L

    @SuppressLint("SuspiciousIndentation")
    fun initialize(context: Context, city: City): Boolean {

        if (!doesFileExist(city.getFileName(), context)) return false

        val hourlyWeatherForecastJSONString = loadActiveCityForecastFromJSON(city.getFileName(), context)
        val hourlyWeatherForecastJSONObject = JSONArray(hourlyWeatherForecastJSONString)

        val actualWeather = hourlyWeatherForecastJSONObject.getJSONObject(0)
        val forecastData = hourlyWeatherForecastJSONObject.getJSONObject(1).getJSONArray("list")

        if (forecastData.length() < 1) return false

        forecastItems = mutableListOf()

        val actualTime = Instant.now().epochSecond - (Instant.now().epochSecond % UNIX_HOUR)

        if(actualTime - actualWeather.get("dt").toString().toLong() <= 0) forecastItems.add(FutureWeatherForecastItem(actualWeather as JSONObject))

        for (i in 0 until forecastData.length()) {

            var tempTimeStamp = actualTime - forecastData.getJSONObject(i).get("dt").toString().toLong()

            if(tempTimeStamp <= 0 ) forecastItems.add(FutureWeatherForecastItem(forecastData.get(i) as JSONObject))
        }
        return true
    }

    fun loadActiveCityForecastFromJSON(fileName: String, context: Context): String {
        return context.openFileInput(fileName).bufferedReader().use { it.readText() }
    }

    fun getForecast(): MutableList<FutureWeatherForecastItem> {
        return forecastItems
    }

    fun doesFileExist(fileName: String, context : Context) : Boolean{
        val file = context.getFileStreamPath(fileName)
        return file.exists()
    }
}