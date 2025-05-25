package com.example.weatherapp

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private lateinit var forecastContainer: LinearLayout

    private lateinit var actualWeatherIcon : ImageView
    private lateinit var actualWeatherCityName : TextView
    private lateinit var actualWeatherDate : TextView
    private lateinit var actualWeatherTime : TextView
    private lateinit var actualWeatherTemperature : TextView

    private lateinit var moreInfoLon : TextView
    private lateinit var moreInfoLat : TextView
    private lateinit var moreInfoPressure : TextView
    private lateinit var moreInfoRain : TextView
    private lateinit var moreInfoVisibility : TextView
    private lateinit var moreInfoHumidity : TextView
    private lateinit var moreInfoWindSpeed : TextView
    private lateinit var moreInfoWindDirection : TextView

    private lateinit var actualCity : City

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        actualCity = City()
        setup()

        val futureWeatherForecast = FutureWeatherForecast()

        if (!futureWeatherForecast.initialize(this)) Log.d("XD", "LOL")
        else {
            val forecast: MutableList<FutureWeatherForecastItem> =
                futureWeatherForecast.getForecast()
            setActualWeather(forecast[0])
            setMoreInfo(forecast[0])
            setFutureForecast(forecast)
        }
    }

    fun setup(){
        forecastContainer = findViewById(R.id.forecastContainer)
        actualWeatherIcon = findViewById(R.id.weatherIcon)
        actualWeatherCityName = findViewById(R.id.cityName)
        actualWeatherDate = findViewById(R.id.date)
        actualWeatherTime = findViewById(R.id.time)
        actualWeatherTemperature = findViewById(R.id.temperature)
        moreInfoLat = findViewById(R.id.latitudeVal)
        moreInfoLon = findViewById(R.id.longitudeVal)
        moreInfoPressure = findViewById(R.id.pressureVal)
        moreInfoRain = findViewById(R.id.rainVal)
        moreInfoVisibility = findViewById(R.id.visibilityVal)
        moreInfoHumidity = findViewById(R.id.humidityVal)
        moreInfoWindSpeed = findViewById(R.id.windVal)
        moreInfoWindDirection = findViewById(R.id.windDirectionVal)
    }

    @SuppressLint("SetTextI18n")
    fun setFutureForecast(forecast: MutableList<FutureWeatherForecastItem>) {
        for (i in forecast) {
            Log.d("XD3" , "x")
            val itemView =
                layoutInflater.inflate(R.layout.forecast_item, forecastContainer, false)
            itemView.findViewById<TextView>(R.id.forecastItemName).text = i.getTime()
            itemView.findViewById<TextView>(R.id.forecastItemTemperature).text =
                i.getTemperature()
            setIcon(itemView.findViewById<ImageView>(R.id.forecastItemIcon), i)
            forecastContainer.addView(itemView)
        }
    }

    fun setIcon(imageView: ImageView, forecastItem : FutureWeatherForecastItem) {
        when (forecastItem.getWeatherType()) {
            2 -> imageView.setImageResource(R.drawable.storm)

            3 -> imageView.setImageResource(R.drawable.drizzle)

            5 -> imageView.setImageResource(R.drawable.rain)

            6 -> imageView.setImageResource(R.drawable.snow)

            7 -> imageView.setImageResource(R.drawable.fog)

            800 -> {
                if(forecastItem.getHour() > 6 && forecastItem.getHour() < 21) imageView.setImageResource(R.drawable.day)
                else imageView.setImageResource(R.drawable.night)
            }

            801,802 -> {
                if(forecastItem.getHour() > 6 && forecastItem.getHour() < 21) imageView.setImageResource(R.drawable.few_clouds)
                else imageView.setImageResource(R.drawable.cloudy_night)
            }

            803 -> {
                if(forecastItem.getHour() > 6 && forecastItem.getHour() < 21) imageView.setImageResource(R.drawable.clouds)
                else imageView.setImageResource(R.drawable.cloudy_night)
            }

            804 -> imageView.setImageResource(R.drawable.full_clouds)
        }
    }

    @SuppressLint("SetTextI18n")
    fun setActualWeather(actualWeather : FutureWeatherForecastItem){
        actualWeatherTime.text = actualWeather.getTime()
        setIcon(actualWeatherIcon, actualWeather)
        actualWeatherTemperature.text = actualWeather.getTemperature()
        actualWeatherTime.text = actualWeather.getTime()
        actualWeatherDate.text = actualWeather.getDate()
        actualWeatherCityName.text = actualCity.getCityName()
    }

    @SuppressLint("SetTextI18n")
    fun setMoreInfo(actualWeather : FutureWeatherForecastItem){
        moreInfoLon.text = actualCity.getLon().toString()
        moreInfoLat.text = actualCity.getLat().toString()
        moreInfoPressure.text = actualWeather.getPressure().toString() + " hPa"
        moreInfoRain.text = actualWeather.getRain().toString() + " mm/m3"
        moreInfoVisibility.text = actualWeather.getVisibility().toString() + " m"
        moreInfoHumidity.text = actualWeather.getHumidity().toString() + "%"
        moreInfoWindSpeed.text = actualWeather.getWindSpeed().toString() + " m/s"
        moreInfoWindDirection.text = actualWeather.getWindDirection()
    }
}