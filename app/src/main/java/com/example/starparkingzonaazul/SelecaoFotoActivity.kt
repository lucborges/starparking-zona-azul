package com.example.starparkingzonaazul

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.starparkingzonaazul.databinding.ActivitySelecaoFotoBinding

class  SelecaoFotoActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySelecaoFotoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySelecaoFotoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.imgFrente.setOnClickListener{
            abrirTelaPreview()
        }
        binding.imgLadoDireito.setOnClickListener{
            abrirTelaPreview()
        }
        binding.imgLadoEsquerdo.setOnClickListener{
            abrirTelaPreview()
        }
        binding.imgTras.setOnClickListener{
            abrirTelaPreview()
        }
        binding.btnCancelar.setOnClickListener{
            finish()
        }
        binding.btnOk.setOnClickListener{
            onBackPressed()
        }
    }

    private fun abrirTelaPreview() {
        val intentCameraPreview = Intent(this, CameraPreviewActivity::class.java)
        startActivity(intentCameraPreview)
    }


}