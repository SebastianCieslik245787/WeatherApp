package com.example.weatherapp

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.core.content.edit
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class SettingsFragment : Fragment(), IFragment {
    private lateinit var unitSpinner: Spinner

    private var loadListener: IFragmentLoadListener? = null

    private var cityChangedListener: IOnActiveCityChangedListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is IOnActiveCityChangedListener) {
            cityChangedListener = context
        } else {
            throw RuntimeException("$context must implement OnActiveCityChangedListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        cityChangedListener = null
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return return inflater.inflate(R.layout.settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setup(view)

        loadListener?.onFragmentLoaded()
    }

    override fun setup(view : View){
        unitSpinner = view.findViewById(R.id.unitSpinner)
        setSpinner()
    }

    private fun setSpinner(){
        val adapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.units_array,
            android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        unitSpinner.adapter = adapter

        val sharedPref = requireActivity().getSharedPreferences("settings", AppCompatActivity.MODE_PRIVATE)
        val savedUnit = sharedPref.getString("unit_preference", "metric")
        val spinnerPosition = adapter.getPosition(savedUnit)

        unitSpinner.setSelection(spinnerPosition)

        unitSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedUnit = parent.getItemAtPosition(position).toString()
                sharedPref.edit {
                    putString("unit_preference", selectedUnit)
                }
                lifecycleScope.launch{
                    APIController.refreshActiveCity(requireContext())
                }
                cityChangedListener?.onActiveCityChanged()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    override fun setLoadListener(listener: IFragmentLoadListener) {
        loadListener = listener
    }
}