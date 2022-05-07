package com.example.starparkingzonaazul

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.FirebaseFunctionsException
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
import com.google.gson.GsonBuilder
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.*

class RegistrarIrregularidadeActivity : AppCompatActivity() {

    private lateinit var etPlaca: TextInputEditText
    private lateinit var btnOpenCamera: MaterialButton
    private lateinit var btnConfirmar: MaterialButton

    private val logEntry = "CADASTRO_PLACA"
    private lateinit var functions: FirebaseFunctions
    private val gson = GsonBuilder().enableComplexMapKeySerialization().create()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registrar_irregularidade)

        functions = Firebase.functions("southamerica-east1")
        etPlaca = findViewById(R.id.etPlaca)
        btnConfirmar = findViewById(R.id.btnConfirmar)
        btnOpenCamera = findViewById(R.id.btnOpenCamera)

        btnConfirmar.setOnClickListener {
            val p = Placa(etPlaca.text.toString())
            val data = DateTimeFormatter
                .ofPattern("dd-MM-yyyy")
                .withZone(ZoneOffset.UTC)
                .format(LocalDateTime.now())
            val horaDeEntrada = DateTimeFormatter
                .ofPattern("HH:mm:ss")
                .withZone(ZoneOffset.UTC)
                .format(LocalDateTime.now())
            val horaDeSaida = horaDeEntrada

            cadastrarPlaca(p, data, horaDeEntrada, horaDeSaida)
                .addOnCompleteListener(OnCompleteListener { task ->
                    if (!task.isSuccessful) {
                        val e = task.exception
                        if (e is FirebaseFunctionsException) {
                            val code = e.code
                            val details = e.details
                        }

                    } else {
                        val genericResp =
                            gson.fromJson(task.result, FunctionsGenericResponse::class.java)

                        Log.i(logEntry, genericResp.status.toString())
                        Log.i(logEntry, genericResp.message.toString())
                        Log.i(logEntry, genericResp.payload.toString())

                        val insertInfo = gson.fromJson(
                            genericResp.payload.toString(),
                            GenericInsertResponse::class.java
                        )
                        Snackbar.make(
                            btnConfirmar, "Placa cadastrada com sucesso: " + insertInfo.docId.toString(),
                            Snackbar.LENGTH_LONG
                        ).show()
                        etPlaca.text!!.clear()
                    }
                })
        }


        btnOpenCamera.setOnClickListener {
            cameraProviderResult.launch(android.Manifest.permission.CAMERA)
            cameraProviderResult.launch(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
    }

    private val cameraProviderResult =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            if (it) {
                abrirTelaDeSelecaoFoto()
            }
        }

    private fun abrirTelaDeSelecaoFoto(){
        val intentSelecaoFoto = Intent(this, SelecaoFotoActivity::class.java)
        startActivity(intentSelecaoFoto)
    }

    private fun cadastrarPlaca(p: Placa, data: String, horaDeEntrada: String, horaDeSaida: String): Task<String> {
        val data = hashMapOf(
            "placa" to p.placa,
            "data" to data,
            "horaDeEntrada" to horaDeEntrada,
            "horaDeSaida" to horaDeSaida
        )
        return functions
            .getHttpsCallable("addNewPlaca")
            .call(data)
            .continueWith { task ->
                val res = gson.toJson(task.result?.data)
                res
            }
    }
}