package com.example.weatherapp

class City {
    private var cityName : String = "Warszawa"
    private var lon : Double = 21.017532
    private var lat : Double = 52.237049

    fun getCityName() : String{
        return this.cityName
    }

    fun getLon() : Double{
        return this.lon
    }

    fun getLat() : Double{
        return this.lat
    }
}