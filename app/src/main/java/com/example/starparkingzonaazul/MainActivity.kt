package com.example.starparkingzonaazul

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.starparkingzonaazul.databinding.ActivityIrregularityBinding
import com.example.starparkingzonaazul.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_StarparkingZonaAzul)
        setContentView(R.layout.activity_main)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.acessButtonMap.setOnClickListener{
            acessMap()
        }

        binding.acessButtonSearch.setOnClickListener{
            acessConsult()
        }

        binding.acessButtonIrregularity.setOnClickListener{
            acessIrregularity()
        }
    }

    private fun acessMap(){
        val intentMap = Intent(this, MapActivity::class.java)
        startActivity(intentMap)
    }

    private fun acessConsult(){
        val intentConsult = Intent(this, SearchActivity::class.java)
        startActivity(intentConsult)
    }

    private fun acessIrregularity(){
        val intentIrregularity = Intent(this, IrregularityActivity::class.java)
        startActivity(intentIrregularity)
    }
}