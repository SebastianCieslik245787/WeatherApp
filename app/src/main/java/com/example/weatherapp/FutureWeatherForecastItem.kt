package com.example.weatherapp

import android.util.Log
import org.json.JSONObject
import kotlin.math.roundToInt

class FutureWeatherForecastItem(data: JSONObject) {
    private var date: String = data.get("dt_txt").toString().replace("-", ".")
    private var temperature: Double = data.getJSONObject("main").getDouble("temp")
    private var weatherType: Int =  data.getJSONArray("weather").getJSONObject(0).getInt("id")
    private var pressure : Int = data.getJSONObject("main").getInt("pressure")
    private var humidity : Int = data.getJSONObject("main").getInt("humidity")
    private var windSpeed : Double = data.getJSONObject("wind").getDouble("speed")
    private var windDeg : Int = data.getJSONObject("wind").getInt("deg")
    private var rain: Double = data.optJSONObject("rain")?.optDouble("1h") ?: 0.0
    private var visibility : Int = data.getInt("visibility")
    private var partOfDay : String = data.getJSONObject("sys").get("pod").toString()

    fun getTime(): String {
        return date.substring(11,16)
    }

    fun getPartOfDay() : String{
        return this.partOfDay
    }

    fun getDate() : String{
        return this.date.substring(0,10)
    }

    fun getPressure() : Int{
        return this.pressure
    }

    fun getHumidity() : Int{
        return this.humidity
    }

    fun getWindSpeed() : Double{
        return this.windSpeed
    }

    fun getWindDirection() : String{
        return if(windDeg == 0) "North"
        else if(windDeg == 90) "East"
        else if(windDeg == 180) "South"
        else if(windDeg == 270) "West"
        else if(windDeg < 90) "North/East"
        else if(windDeg < 180) "South/East"
        else if(windDeg < 270) "South/West"
        else " North/West"
    }

    fun getTemperature() : String{
        return this.temperature.roundToInt().toString()
    }

    fun getWeatherType() : Int{
        if(this.weatherType > 799) return this.weatherType
        return this.weatherType / 100
    }

    fun getRain() : Double{
        return this.rain
    }

    fun getVisibility() : Int{
        return this.visibility
    }
}
