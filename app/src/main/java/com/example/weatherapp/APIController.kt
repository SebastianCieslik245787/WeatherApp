package com.example.weatherapp

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONObject
import kotlin.collections.mutableListOf

class APIController {
    companion object {
        private var API_KEY = BuildConfig.WEATHER_API_KEY
        private var limit: Int = 5

        suspend fun findCities(name: String, context: Context): MutableList<City>? =
            withContext(Dispatchers.IO) {
                try {
                    val client = OkHttpClient()
                    val url =
                        "https://api.openweathermap.org/geo/1.0/direct?q=${name}&limit=$limit&appid=$API_KEY"
                    val request = Request.Builder().url(url).build()

                    client.newCall(request).execute().use { response ->
                        if (!response.isSuccessful) {
                            withContext(Dispatchers.Main) {
                                Toast.makeText(
                                    context,
                                    "Failed to fetch city data",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            return@withContext null
                        }
                        val body = response.body?.string() ?: return@withContext null
                        val jsonArray = JSONArray(body)
                        val cities = mutableListOf<City>()
                        for (i in 0 until jsonArray.length()) {
                            val obj = jsonArray.getJSONObject(i)
                            cities.add(City(obj, false))
                        }
                        cities
                    }
                } catch (_: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            context,
                            "No internet connection!\nCan't fetch data from server!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    null
                }
            }

        suspend fun refreshCityForecast(city: City, context: Context, unit: String) =
            withContext(Dispatchers.IO) {
                try {
                    val client = OkHttpClient()

                    val currentUrl =
                        "https://api.openweathermap.org/data/2.5/weather?lat=${city.getLat()}&lon=${city.getLon()}&appid=$API_KEY&units=$unit"
                    val currentRequest = Request.Builder().url(currentUrl).build()

                    val currentResponseData = client.newCall(currentRequest).execute().use { response ->
                        if (!response.isSuccessful) null else response.body?.string()
                    }

                    val forecastUrl =
                        "https://pro.openweathermap.org/data/2.5/forecast/hourly?lat=${city.getLat()}&lon=${city.getLon()}&appid=$API_KEY&units=$unit"
                    val forecastRequest = Request.Builder().url(forecastUrl).build()

                    val forecastResponseData = client.newCall(forecastRequest).execute().use { response ->
                        if (!response.isSuccessful) {
                            withContext(Dispatchers.Main) {
                                Toast.makeText(
                                    context,
                                    "Failed to fetch weather forecast data",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            null
                        } else {
                            response.body?.string()
                        }
                    }

                    if (currentResponseData != null && forecastResponseData != null) {
                        val combinedJson = "[${currentResponseData.trim()}, ${forecastResponseData.trim()}]"

                        val fileName = city.getFileName()
                        context.openFileOutput(fileName, Context.MODE_PRIVATE).use { output ->
                            output.write(combinedJson.toByteArray())
                        }
                    }

                } catch (_: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            context,
                            "No internet connection!\nCan't fetch weather data",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }

        suspend fun refreshAllFavouriteCities(context: Context) =
            withContext(Dispatchers.IO) {
                val sharedPrefFC = context.getSharedPreferences("favourites", Context.MODE_PRIVATE)
                val jsonString = sharedPrefFC.getString("favourites_list", null) ?: return@withContext
                val jsonArray = JSONArray(jsonString)
                val cityList = mutableListOf<City>()
                val unit = getUnitSP(context)

                for (i in 0 until jsonArray.length()) {
                    val cityObject = jsonArray.getJSONObject(i)
                    cityList.add(City(cityObject, true))
                }

                val jobs = cityList.map { city ->
                    async {
                        try {
                            refreshCityForecast(city, context, unit)
                            Log.d("Refresh", city.getCityName())
                        } catch (_: Exception) {
                            withContext(Dispatchers.Main) {
                                Toast.makeText(
                                    context,
                                    "Błąd przy pobieraniu dla ${city.getCityName()}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                }

                jobs.awaitAll()
                Log.d("Refresh", "DONE")
            }

        suspend fun refreshActiveCity(context: Context) = withContext(Dispatchers.IO) {
            val sharedPreferences = context.getSharedPreferences("actualCity", Context.MODE_PRIVATE)
            val actualCityJSON = sharedPreferences?.getString("actualCity", null)

            if (actualCityJSON == null) return@withContext

            val activeCity = City(JSONObject(actualCityJSON), true)

            refreshCityForecast(activeCity, context, getUnitSP(context))
        }

        fun getUnitSP(context: Context) : String{
            val sharedPref = context.getSharedPreferences("settings", AppCompatActivity.MODE_PRIVATE)
            return sharedPref.getString("unit_preference", "standard").toString()
        }
    }
}
