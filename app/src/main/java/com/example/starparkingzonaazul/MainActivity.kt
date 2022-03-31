package com.example.starparkingzonaazul

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.starparkingzonaazul.fragments.HomeFragment
import com.example.starparkingzonaazul.fragments.IrregularityFragment
import com.example.starparkingzonaazul.fragments.MapFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_StarparkingZonaAzul)
        setContentView(R.layout.activity_main)

        val homeFragment=HomeFragment()
        val irregularityFragment=IrregularityFragment()
        val mapFragment=MapFragment()
        setCurrentFragment(homeFragment)

        findViewById<BottomNavigationView>(R.id.bottom_navigation)
            .setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.home->setCurrentFragment(homeFragment)
                R.id.irregularity->setCurrentFragment(irregularityFragment)
                R.id.map->setCurrentFragment(mapFragment)

            }
            true
        }

    }

    private fun setCurrentFragment(fragment:Fragment)=
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragment_container,fragment)
            commit()
        }




}