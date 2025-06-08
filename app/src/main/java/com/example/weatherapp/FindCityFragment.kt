package com.example.weatherapp

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import org.json.JSONArray
import org.json.JSONObject
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class FindCityFragment : Fragment(), IFragment {
    private var loadListener: IFragmentLoadListener? = null
    private lateinit var inputCityName: EditText
    private lateinit var favouriteCities: LinearLayout
    private lateinit var foundCities: LinearLayout
    private lateinit var actualCity: LinearLayout
    private lateinit var buttonFind: ImageButton
    private var favouriteCitiesArr: MutableList<City> = mutableListOf()
    private lateinit var foundCitiesArr: MutableList<City>
    private lateinit var activeCityView: View
    private var activeCity: City? = null

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

        setActiveCityFromSP(requireContext())

        loadListener?.onFragmentLoaded()
    }

    override fun setLoadListener(listener: IFragmentLoadListener) {
        loadListener = listener
    }

    override fun setup(view: View) {
        inputCityName = view.findViewById(R.id.findCity)
        favouriteCities = view.findViewById(R.id.favourite)
        foundCities = view.findViewById(R.id.foundCities)
        actualCity = view.findViewById(R.id.actualCity)
        buttonFind = view.findViewById(R.id.findCityButton)
        buttonFind.setOnClickListener {
            findCity()
            inputCityName.text.clear()
        }
    }

    private fun setActiveCityFromSP(context: Context) {
        val sharedPref = context.getSharedPreferences("actualCity", Context.MODE_PRIVATE)
        val jsonString = sharedPref.getString("actualCity", null)

        if (jsonString == null) return

        val activeCityJSON = JSONObject(jsonString.toString())

        val temp = City(activeCityJSON, false)
        temp.toggleActive()
        if (findInFavourites(temp)) temp.toggleFavourite()
        setActualCity(temp)
    }


    private fun getFavouriteCities(context: Context): MutableList<City> {
        val sharedPref = context.getSharedPreferences("favourites", Context.MODE_PRIVATE)
        val jsonString = sharedPref.getString("favourites_list", null) ?: return mutableListOf()
        val jsonArray = JSONArray(jsonString)
        val result = mutableListOf<City>()

        for (i in 0 until jsonArray.length()) {
            val cityObject = jsonArray.getJSONObject(i)
            result.add(City(cityObject, true))
        }

        return result
    }

    private fun setFavourite() {
        favouriteCitiesArr = getFavouriteCities(requireContext())

        favouriteCities.removeAllViews()

        if (favouriteCitiesArr.isEmpty()) {
            layoutInflater.inflate(R.layout.empty_data, favouriteCities, true)
            return
        }

        for (i in favouriteCitiesArr) {
            val itemView = layoutInflater.inflate(R.layout.favourite_item, favouriteCities, false)
            itemView.findViewById<TextView>(R.id.cityName).text = i.getCityInfo()
            itemView.findViewById<ImageButton>(R.id.favouriteButton)
                .setOnClickListener { deleteFromFavourite(i, requireContext()) }
            itemView.setOnClickListener { setActualCity(i) }
            favouriteCities.addView(itemView)
        }
    }

    private fun setActualCitySP(city: City) {
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

    //Aktualne miasto widok
    private fun setActualCity(city: City) {
        setActualCitySP(city)
        lifecycleScope.launch {
            APIController.refreshActiveCity(requireContext())
        }
        activeCityView = layoutInflater.inflate(R.layout.favourite_item, actualCity, false)
        activeCityView.findViewById<TextView>(R.id.cityName).text = city.getCityInfo()
        activeCityView.findViewById<ImageButton>(R.id.favouriteButton)
            .setOnClickListener { toggleFavourite(city, requireContext(), activeCityView) }
        if (findInFavourites(city)) {
            activeCityView.findViewById<ImageButton>(R.id.favouriteButton)
                .setImageResource(R.drawable.favourite)
        } else activeCityView.findViewById<ImageButton>(R.id.favouriteButton)
            .setImageResource(R.drawable.heart)
        actualCity.removeAllViews()
        actualCity.addView(activeCityView)
        city.toggleActive()
        activeCity = city
    }

    //pobieranie miast z wyszukiwarki
    private fun findCity() {
        foundCities.removeAllViews()

        val cityName = inputCityName.text.toString()

        lifecycleScope.launch {
            val res = APIController.findCities(cityName, requireContext())
            if (res == null || res.isEmpty()) {
                layoutInflater.inflate(R.layout.empty_data, foundCities, true)
                return@launch
            }

            foundCitiesArr = res.toMutableList()
            for (i in foundCitiesArr) {
                val itemView =
                    layoutInflater.inflate(R.layout.founded_city_item, foundCities, false)
                itemView.findViewById<TextView>(R.id.foundCityName).text = i.getCityName()
                itemView.findViewById<TextView>(R.id.foundCityRegion).text = i.getCityRegion()
                itemView.setOnClickListener {
                    setActualCity(i)
                    foundCities.removeAllViews()
                }
                foundCities.addView(itemView)
            }
        }
    }

    private fun addToFavourite(city: City, context: Context) {
        val sharedPref = context.getSharedPreferences("favourites", Context.MODE_PRIVATE)
        val existing = sharedPref.getString("favourites_list", null)

        val jsonArray = if (existing != null) JSONArray(existing) else JSONArray()

        jsonArray.put(city.toJSON())
        sharedPref.edit { putString("favourites_list", jsonArray.toString()) }
        setFavourite()
    }

    private fun deleteFromFavourite(city: City, context: Context) {
        val sharedPref = context.getSharedPreferences("favourites", Context.MODE_PRIVATE)
        val updatedList = favouriteCitiesArr.filterNot {
            it.equals(city)
        }
        val jsonArray = JSONArray()
        for (city in updatedList) {
            jsonArray.put(city.toJSON())
        }
        sharedPref.edit { putString("favourites_list", jsonArray.toString()) }
        if (city.equals(activeCity)) {
            activeCityView.findViewById<ImageButton>(R.id.favouriteButton)
                .setImageResource(R.drawable.heart)
        }
        setFavourite()
    }

    private fun toggleFavourite(city: City, context: Context, item: View) {
        if (city.isFavourite()) {
            item.findViewById<ImageButton>(R.id.favouriteButton).setImageResource(R.drawable.heart)
            deleteFromFavourite(city, context)
        } else {
            item.findViewById<ImageButton>(R.id.favouriteButton)
                .setImageResource(R.drawable.favourite)
            addToFavourite(city, context)
        }
        city.toggleFavourite()
        setFavourite()
    }

    private fun findInFavourites(city: City?): Boolean {
        if (city == null) return false
        return favouriteCitiesArr.find {
            it.equals(city)
        } != null
    }
}