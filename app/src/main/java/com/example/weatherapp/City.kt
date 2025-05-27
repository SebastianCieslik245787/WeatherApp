package com.example.weatherapp

import org.json.JSONObject

class City(data: JSONObject, favourite : Boolean) {
    private var cityName : String = data.getString("name")
    private var cityRegion : String = data.getString("state")
    private var lon : Double = data.getDouble("lon")
    private var lat : Double = data.getDouble("lat")
    private var isFavourite : Boolean = favourite
    private var isActive : Boolean = false
    private var fileName : String = data.getString("filename")


    fun getCityName() : String{
        return this.cityName
    }

    fun getLon() : Double{
        return this.lon
    }

    fun getLat() : Double{
        return this.lat
    }

    fun getCityRegion() : String{
        return this.cityRegion
    }

    fun getCityInfo() : String{
        return "$cityName ($cityRegion)"
    }

    fun getFileName(): String{
        return this.fileName
    }

    fun toggleActive(){
        this.isActive = !this.isActive
    }

    fun toggleFavourite(){
        this.isFavourite = !this.isFavourite
    }
}