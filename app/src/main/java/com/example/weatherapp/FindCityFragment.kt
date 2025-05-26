package com.example.weatherapp

import android.view.View
import androidx.fragment.app.Fragment

class FindCityFragment : Fragment(), IFragment {
    private var loadListener: IFragmentLoadListener? = null

    override fun setLoadListener(listener: IFragmentLoadListener) {
        loadListener = listener
    }

    override fun setup(view: View) {
        TODO("Not yet implemented")
    }
}