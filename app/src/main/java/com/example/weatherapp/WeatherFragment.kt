package com.example.weatherapp

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONObject

import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class WeatherFragment : Fragment(), IFragment {
    private lateinit var forecastContainer: LinearLayout
    private lateinit var weatherLayout: LinearLayout

    private lateinit var actualWeatherIcon : ImageView
    private lateinit var refreshButton : ImageView
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

    private lateinit var actualCity: City

    private lateinit var temperatureUnit : String
    private lateinit var windSpeedUnit : String
    private lateinit var actualUnit : String

    private var loadListener: IFragmentLoadListener? = null

    fun refreshForecast() {
        // Po prostu wywołujemy istniejącą logikę ładowania z flagą refresh = true
        loadForecast(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.forecast, container, false)
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setup(view)
        loadForecast()
    }

    override fun setup(view: View) {
        forecastContainer = view.findViewById(R.id.forecastContainer)
        weatherLayout = view.findViewById(R.id.weatherLayout)
        actualWeatherIcon = view.findViewById(R.id.weatherIcon)
        actualWeatherCityName = view.findViewById(R.id.cityName)
        actualWeatherDate = view.findViewById(R.id.date)
        actualWeatherTime = view.findViewById(R.id.time)
        actualWeatherTemperature = view.findViewById(R.id.temperature)
        moreInfoLat = view.findViewById(R.id.latitudeVal)
        moreInfoLon = view.findViewById(R.id.longitudeVal)
        moreInfoPressure = view.findViewById(R.id.pressureVal)
        moreInfoRain = view.findViewById(R.id.rainVal)
        moreInfoVisibility = view.findViewById(R.id.visibilityVal)
        moreInfoHumidity = view.findViewById(R.id.humidityVal)
        moreInfoWindSpeed = view.findViewById(R.id.windVal)
        moreInfoWindDirection = view.findViewById(R.id.windDirectionVal)
        refreshButton = view.findViewById(R.id.refreshIcon)
        refreshButton.setOnClickListener { loadForecast(true) }
    }

    @SuppressLint("SetTextI18n")
    private fun setFutureForecast(forecast: MutableList<FutureWeatherForecastItem>) {
        for (i in 1 until forecast.size) {
            val itemView = layoutInflater.inflate(R.layout.forecast_item, forecastContainer, false)
            itemView.findViewById<TextView>(R.id.forecastItemName).text = forecast[i].getTime()
            itemView.findViewById<TextView>(R.id.forecastItemTemperature).text = forecast[i].getTemperature() + temperatureUnit
            setIcon(itemView.findViewById(R.id.forecastItemIcon), forecast[i])
            forecastContainer.addView(itemView)
        }
    }

    private fun setIcon(imageView: ImageView, forecastItem: FutureWeatherForecastItem) {
        when (forecastItem.getWeatherType()) {
            2 -> imageView.setImageResource(R.drawable.storm)
            3 -> imageView.setImageResource(R.drawable.drizzle)
            5 -> imageView.setImageResource(R.drawable.rain)
            6 -> imageView.setImageResource(R.drawable.snow)
            7 -> imageView.setImageResource(R.drawable.fog)
            800 -> {
                if (forecastItem.getPartOfDay() == "d")
                    imageView.setImageResource(R.drawable.day)
                else
                    imageView.setImageResource(R.drawable.night)
            }
            801, 802 -> {
                if (forecastItem.getPartOfDay() == "d")
                    imageView.setImageResource(R.drawable.few_clouds)
                else
                    imageView.setImageResource(R.drawable.cloudy_night)
            }
            803 -> {
                if (forecastItem.getPartOfDay() == "d")
                    imageView.setImageResource(R.drawable.clouds)
                else
                    imageView.setImageResource(R.drawable.cloudy_night)
            }
            804 -> imageView.setImageResource(R.drawable.full_clouds)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setActualWeather(actualWeather: FutureWeatherForecastItem) {
        actualWeatherTime.text = actualWeather.getTime()
        setIcon(actualWeatherIcon, actualWeather)
        actualWeatherTemperature.text = actualWeather.getTemperature() + temperatureUnit
        actualWeatherDate.text = actualWeather.getDate()
        actualWeatherCityName.text = actualCity.getCityName()
    }

    @SuppressLint("SetTextI18n")
    private fun setMoreInfo(actualWeather: FutureWeatherForecastItem) {
        moreInfoLon.text = actualCity.getLon().toString()
        moreInfoLat.text = actualCity.getLat().toString()
        moreInfoPressure.text = actualWeather.getPressure().toString() + " hPa"
        moreInfoRain.text = actualWeather.getRain().toString() + " mm/m3"
        moreInfoVisibility.text = actualWeather.getVisibility() + " m"
        moreInfoHumidity.text = actualWeather.getHumidity().toString() + "%"
        moreInfoWindSpeed.text = actualWeather.getWindSpeed().toString() + windSpeedUnit
        moreInfoWindDirection.text = actualWeather.getWindDirection()
    }

    private fun getUnits(){
        val sharedPref = requireActivity().getSharedPreferences("settings", AppCompatActivity.MODE_PRIVATE)
        val savedUnit : String = sharedPref.getString("unit_preference", "metric").toString()
        if(savedUnit == "imperial"){
            temperatureUnit = " °F"
            windSpeedUnit = " mi/s"
        }
        else if(savedUnit == "metric"){
            temperatureUnit = " °C"
            windSpeedUnit = " m/s"
        }
        else{
            temperatureUnit = " K"
            windSpeedUnit = " m/s"
        }
        actualUnit = savedUnit
    }

    override fun setLoadListener(listener: IFragmentLoadListener) {
        loadListener = listener
    }

    private fun setEmptyData(){
        Handler(Looper.getMainLooper()).post {
            weatherLayout.removeAllViews()
            val emptyView = layoutInflater.inflate(R.layout.empty_data, weatherLayout, false)
            weatherLayout.addView(emptyView)
            loadListener?.onFragmentLoaded()
        }
    }

    private fun loadForecast(refresh : Boolean = false){
        getUnits()
        if(refresh) loadListener?.onFragmentLoading()

        val sharedPreferences = context?.getSharedPreferences("actualCity", Context.MODE_PRIVATE)
        val actualCityJSON = sharedPreferences?.getString("actualCity", null)

        if (actualCityJSON == null) {
            setEmptyData()
            return
        }

        actualCity = City(JSONObject(actualCityJSON), true)

        viewLifecycleOwner.lifecycleScope.launch {
            if(refresh) APIController.refreshCityForecast(actualCity, requireContext(), actualUnit)
            val futureWeatherForecast = FutureWeatherForecast()
            val initialized = futureWeatherForecast.initialize(requireContext(), actualCity)

            if (!initialized) {
                setEmptyData()
                return@launch
            }

            val forecast: MutableList<FutureWeatherForecastItem> = futureWeatherForecast.getForecast()

            forecastContainer.removeAllViews()
            setActualWeather(forecast[0])
            setMoreInfo(forecast[0])
            setFutureForecast(forecast)
            loadListener?.onFragmentLoaded()
        }
    }
}