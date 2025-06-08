package com.example.weatherapp

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import kotlin.collections.mutableListOf

class APIController {
    companion object{
        private var API_KEY = BuildConfig.WEATHER_API_KEY
        private var limit : Int = 5
        suspend fun findCities(name: String): MutableList<City>? = withContext(Dispatchers.IO) {
            val client = OkHttpClient()
            val url = "https://api.openweathermap.org/geo/1.0/direct?q=${name}&limit=$limit&appid=$API_KEY"
            val request = Request.Builder().url(url).build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) return@withContext null
                val body = response.body?.string() ?: return@withContext null
                val jsonArray = JSONArray(body)
                val cities = mutableListOf<City>()
                for (i in 0 until jsonArray.length()) {
                    val obj = jsonArray.getJSONObject(i)
                    cities.add(City(obj, false))
                }
                cities
            }
        }
    }
}