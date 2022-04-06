package com.example.starparkingzonaazul

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_StarparkingZonaAzul)
        setContentView(R.layout.activity_main)

        val buttonMap:Button = findViewById(R.id.acessButtonMap)
        val buttonSearch:Button = findViewById(R.id.acessButtonSearch)
        val buttonIrregularity:Button = findViewById(R.id.acessButtonIrregularity)

        buttonMap.setOnClickListener {
            val intentMap = Intent(this, MapActivity::class.java)
            startActivity(intentMap)
        }

        buttonSearch.setOnClickListener{
            val intentSearch = Intent(this, SearchActivity::class.java)
            startActivity(intentSearch)
        }

        buttonIrregularity.setOnClickListener{
            val intentIrregularity = Intent(this, IrregularityActivity::class.java)
            startActivity(intentIrregularity)
        }
    }
}