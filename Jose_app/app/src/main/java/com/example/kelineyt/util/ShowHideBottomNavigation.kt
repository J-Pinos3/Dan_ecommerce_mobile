package com.example.kelineyt.util

import androidx.fragment.app.Fragment
import android.view.View
import com.example.kelineyt.R
import com.example.kelineyt.activities.ShoppingActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

fun Fragment.hideBottomNavigationView(){

    val bottomNavigationView = (activity as ShoppingActivity).findViewById<BottomNavigationView>(
        com.example.kelineyt.R.id.bottomNavigation)
    bottomNavigationView.visibility = android.view.View.GONE

}

fun Fragment.showBottomNavigationView(){

    val bottomNavigationView = (activity as ShoppingActivity).findViewById<BottomNavigationView>(
        com.example.kelineyt.R.id.bottomNavigation)
    bottomNavigationView.visibility = android.view.View.VISIBLE

}