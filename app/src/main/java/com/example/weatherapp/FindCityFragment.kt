package com.example.weatherapp

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import org.json.JSONArray
import org.json.JSONObject

class FindCityFragment : Fragment(), IFragment {
    private var loadListener: IFragmentLoadListener? = null
    private lateinit var inputCityName : AutoCompleteTextView
    private lateinit var favouriteCities : LinearLayout
    private lateinit var actualCity : LinearLayout
    private lateinit var buttonFind : ImageButton
    private var cityArr : MutableList<City> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.find_city, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setup(view)

        setFavourite()

        loadListener?.onFragmentLoaded()
    }

    override fun setLoadListener(listener: IFragmentLoadListener) {
        loadListener = listener
    }

    override fun setup(view: View) {
        inputCityName = view.findViewById(R.id.findCity)
        favouriteCities = view.findViewById(R.id.favourite)
        actualCity = view.findViewById(R.id.actualCity)
        buttonFind = view.findViewById(R.id.findCityButton)
        buttonFind.setOnClickListener { findCity() }

        val cities = listOf("Warszawa\nMazwowieckie", "Llanfairpwllgwyngyllgogerychwyrndrobwllllantysiliogogogoch", "Gdańsk", "Wrocław", "Poznań", "Łódź", "Szczecin")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, cities)

        inputCityName.setAdapter(adapter)
    }

    private fun getCities() : Boolean{
        val favouritesCitiesJSONString = loadJSONFromAsset("city.json", requireContext())
        val data = JSONArray(favouritesCitiesJSONString)

        if (data.length() < 1) return false

        for (i in 0 until data.length()){
            val cityObject = data.getJSONObject(i)
            cityArr.add(City(cityObject))
        }
        return true
    }

    private fun loadJSONFromAsset(fileName: String, context: Context): String {
        return context.assets.open(fileName).bufferedReader().use { it.readText() }
    }

    private fun setFavourite(){
        getCities()

        for(i in cityArr){
            val itemView = layoutInflater.inflate(R.layout.favourite_item, favouriteCities, false)
            itemView.findViewById<TextView>(R.id.cityName).text = i.getCityInfo()
            itemView.setOnClickListener {setActualCity(i, true)}
            favouriteCities.addView(itemView)
        }
    }

    private fun setActualCitySP(city : City){
        val sharedPref = requireActivity().getSharedPreferences("actualCity", Context.MODE_PRIVATE)
        val json = JSONObject().apply {
            put("name", city.getCityName())
            put("state", city.getCityRegion())
            put("lon", city.getLon())
            put("lat", city.getLat())
        }

        sharedPref.edit {
            putString("actualCity", json.toString())
        }
    }

    private fun setActualCity(city : City, isInFavourite : Boolean){
        setActualCitySP(city)
        val itemView = layoutInflater.inflate(R.layout.favourite_item, actualCity, false)
        itemView.findViewById<TextView>(R.id.cityName).text = city.getCityInfo()
        itemView.findViewById<ImageButton>(R.id.favouriteButton).setOnClickListener { deleteFromFavourite(itemView) }
        actualCity.removeAllViews()
        actualCity.addView(itemView)
        if(!isInFavourite){
            itemView.findViewById<ImageButton>(R.id.favouriteButton).setImageResource(R.drawable.favourite)
        }
    }

    private fun deleteFromFavourite(view : View){
        view.findViewById<ImageButton>(R.id.favouriteButton).setImageResource(R.drawable.heart)
    }

    private fun findCity(){

    }
}