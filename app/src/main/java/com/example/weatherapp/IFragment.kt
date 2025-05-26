package com.example.weatherapp

import android.view.View

interface IFragment {
    fun setLoadListener(listener: IFragmentLoadListener)
    fun setup(view : View)
}