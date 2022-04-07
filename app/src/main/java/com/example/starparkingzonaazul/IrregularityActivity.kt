package com.example.starparkingzonaazul

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.starparkingzonaazul.databinding.ActivityIrregularityBinding

class IrregularityActivity : AppCompatActivity() {

    private lateinit var binding : ActivityIrregularityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_StarparkingZonaAzul)

        binding = ActivityIrregularityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.acessButtonRegister.setOnClickListener{
            acessRegistrarIrregularidade()
        }
    }

    private fun acessRegistrarIrregularidade(){
        val intent = Intent(this, RegistrarIrregularidadeActivity::class.java)
        startActivity(intent)
    }
}