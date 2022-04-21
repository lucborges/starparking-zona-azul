package com.example.starparkingzonaazul

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import com.google.android.material.snackbar.Snackbar

class SearchActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_StarparkingZonaAzul)
        setContentView(R.layout.activity_search)

        val snackbarResult = findViewById<View>(R.id.btnPesquisar)
        snackbarResult.setOnClickListener{
            Snackbar.make(snackbarResult, "Veículo regularizado até (horário final)", Snackbar.LENGTH_LONG)
                .show()
        }
    }
}