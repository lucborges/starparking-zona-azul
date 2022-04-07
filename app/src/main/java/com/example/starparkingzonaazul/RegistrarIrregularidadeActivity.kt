package com.example.starparkingzonaazul

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.functions.FirebaseFunctionsException
import com.google.gson.GsonBuilder
import com.google.android.gms.tasks.Task
import android.util.Log
import com.example.starparkingzonaazul.databinding.ActivityRegistrarIrregularidadeBinding
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase

class RegistrarIrregularidadeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegistrarIrregularidadeBinding
    private val logEntry = "CADASTRO_PLACA"
    private lateinit var functions: FirebaseFunctions
    private val gson = GsonBuilder().enableComplexMapKeySerialization().create()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegistrarIrregularidadeBinding.inflate(layoutInflater)
        setContentView(binding.root)
//        functions = Firebase.functions("southamerica-east1")

        binding.btnPlaca.setOnClickListener{

            val p = Placa(binding.etPlaca.text.toString().toDouble())
            cadastrarPlaca(p)
                .addOnCompleteListener(OnCompleteListener { task ->
                    if (!task.isSuccessful) {

                        val e = task.exception
                        if (e is FirebaseFunctionsException) {
                            val code = e.code
                            val details = e.details
                        }

                    }else{
                        val genericResp = gson.fromJson(task.result, FunctionsGenericResponse::class.java)

                        Log.i(logEntry, genericResp.status.toString())
                        Log.i(logEntry, genericResp.message.toString())
                        Log.i(logEntry, genericResp.payload.toString())

                        val insertInfo = gson.fromJson(genericResp.payload.toString(), GenericInsertResponse::class.java)

                        Snackbar.make(binding.btnPlaca, "Placa cadastrada: " + insertInfo.docId,
                            Snackbar.LENGTH_LONG).show();
                        binding.etPlaca.text!!.clear()
                    }
                })
        }
        binding.btnOpenCamera.setOnClickListener {
            cameraProviderResult.launch(android.Manifest.permission.CAMERA)
            cameraProviderResult.launch(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
    }

    private val cameraProviderResult =
        registerForActivityResult(ActivityResultContracts.RequestPermission()){
            if(it) {
                abrirTelaDeSelecaoFoto()
            }else{
                Snackbar.make(binding.root, "Voce nao concedeu permissoes para usar a camera." , Snackbar.LENGTH_INDEFINITE).show()

            }
        }

    private fun abrirTelaDeSelecaoFoto(){
        val intentSelecaoFoto = Intent(this, SelecaoFotoActivity::class.java)
        startActivity(intentSelecaoFoto)
    }

    private fun cadastrarPlaca(p: Placa): Task<String> {
        val data = hashMapOf(
            "name" to p.name
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