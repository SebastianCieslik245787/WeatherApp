package com.example.weatherapp

import android.content.Context
import android.util.Log
import org.json.JSONObject

class City(data: JSONObject, favourite : Boolean) {
    private var cityName : String = data.getString("name")
    private var cityRegion : String = data.getString("state")
    private var lon : Double = data.getDouble("lon")
    private var lat : Double = data.getDouble("lat")
    private var isFavourite : Boolean = favourite
    private var isActive : Boolean = false
    private var fileName : String = createFileName()

    private fun createFileName() : String{
        return "${cityName}_${cityRegion}_${lon}_${lat}.json"
    }

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

    fun isActive() : Boolean{
        return this.isActive
    }

    fun toggleFavourite(){
        this.isFavourite = !this.isFavourite
    }

    fun isFavourite() : Boolean{
        return this.isFavourite
    }

    fun toJSON(): JSONObject {
        return JSONObject().apply {
            put("name", cityName)
            put("state", cityRegion)
            put("lon", lon)
            put("lat", lat)
        }
    }

    fun equals(city: City?) : Boolean{
        if(city == null) return false
        return this.cityName == city.cityName && this.cityRegion == city.cityRegion && this.lon == city.lon && this.lat == city.lat
    }

    fun deleteCityWeatherFile(context: Context){
        val deleted = context.deleteFile(this.fileName)
        if (deleted) {
            Log.d("FileDelete", "File $fileName deleted successfully")
        } else {
            Log.d("FileDelete", "File $fileName not found or couldn't be deleted")
        }
    }
}