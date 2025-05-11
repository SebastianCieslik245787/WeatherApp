package com.example.weatherapp

import org.json.JSONObject
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

class FutureWeatherForecastItem(data: JSONObject) {
    private var name: Long = data.get("dt").toString().toLong()
    private var temperature: String = (data.getJSONObject("main").get("temp").toString())
    private var weatherType: Int =  data.getJSONArray("weather").getJSONObject(0).getInt("id")

    fun getName(): Int {
        val time =  LocalDateTime.ofInstant(
            Instant.ofEpochSecond(name),
            ZoneId.systemDefault()
        )
        return time.hour
    }

    fun getTemperature() : String{
        return "$temperature°C"
    }

    fun getWeatherType() : Int{
        if(weatherType > 799) return weatherType
        return weatherType / 100
    }
}