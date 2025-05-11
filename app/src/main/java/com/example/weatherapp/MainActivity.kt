package com.example.weatherapp

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private lateinit var forecastContainer: LinearLayout

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        forecastContainer = findViewById(R.id.forecastContainer)

        val futureWeatherForecast = FutureWeatherForecast()

        if (!futureWeatherForecast.initialize(this)) Log.d("XD", "LOL")
        else {
            val forecast: MutableList<FutureWeatherForecastItem> =
                futureWeatherForecast.getForecast()

            setFutureForecast(forecast)
        }
    }

    @SuppressLint("SetTextI18n")
    fun setFutureForecast(forecast: MutableList<FutureWeatherForecastItem>) {
        for (i in forecast) {
            val itemView =
                layoutInflater.inflate(R.layout.forecast_item, forecastContainer, false)
            itemView.findViewById<TextView>(R.id.forecastItemName).text = "${i.getName()}:00"
            itemView.findViewById<TextView>(R.id.forecastItemTemperature).text =
                i.getTemperature()
            setForecastItemIcon(itemView, i)
            forecastContainer.addView(itemView)
        }
    }

    fun setForecastItemIcon(itemView: View, forecastItem : FutureWeatherForecastItem) {
        when (forecastItem.getWeatherType()) {
            2 -> itemView.findViewById<ImageView>(R.id.forecastItemIcon)
                .setImageResource(R.drawable.storm)

            3 -> itemView.findViewById<ImageView>(R.id.forecastItemIcon)
                .setImageResource(R.drawable.drizzle)

            5 -> itemView.findViewById<ImageView>(R.id.forecastItemIcon)
                .setImageResource(R.drawable.rain)

            6 -> itemView.findViewById<ImageView>(R.id.forecastItemIcon)
                .setImageResource(R.drawable.snow)

            7 -> itemView.findViewById<ImageView>(R.id.forecastItemIcon)
                .setImageResource(R.drawable.fog)

            800 -> {
                if(forecastItem.getName() > 6 && forecastItem.getName() < 21) itemView.findViewById<ImageView>(R.id.forecastItemIcon)
                    .setImageResource(R.drawable.day)
                else itemView.findViewById<ImageView>(R.id.forecastItemIcon)
                    .setImageResource(R.drawable.night)
            }

            801,802 -> {
                if(forecastItem.getName() > 6 && forecastItem.getName() < 21) itemView.findViewById<ImageView>(R.id.forecastItemIcon)
                    .setImageResource(R.drawable.few_clouds)
                else itemView.findViewById<ImageView>(R.id.forecastItemIcon)
                    .setImageResource(R.drawable.cloudy_night)
            }

            803 -> {
                if(forecastItem.getName() > 6 && forecastItem.getName() < 21) itemView.findViewById<ImageView>(R.id.forecastItemIcon)
                    .setImageResource(R.drawable.clouds)
                else itemView.findViewById<ImageView>(R.id.forecastItemIcon)
                    .setImageResource(R.drawable.cloudy_night)
            }


            804 -> itemView.findViewById<ImageView>(R.id.forecastItemIcon)
                .setImageResource(R.drawable.full_clouds)
        }
    }
}