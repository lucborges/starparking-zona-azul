package com.example.starparkingzonaazul

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import com.example.starparkingzonaazul.databinding.ActivityResgistrarIrregularidadeBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.functions.FirebaseFunctionsException
import com.google.gson.GsonBuilder
import com.google.android.gms.tasks.Task
import android.util.Log
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase


class ResgistrarIrregularidade : AppCompatActivity() {

    private lateinit var binding: ActivityResgistrarIrregularidadeBinding
    private val logEntry = "CADASTRO_PLACA"
    private lateinit var functions: FirebaseFunctions

    private val gson = GsonBuilder().enableComplexMapKeySerialization().create()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // inflar a activity.
        binding = ActivityResgistrarIrregularidadeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        functions = Firebase.functions("southamerica-east1")


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

                        // dar um tratamento adequado...

                    }else{

                        /**
                         * Lembre-se que na Function criamos um padrão de retorno.
                         * Um JSON composto de status, message e payload.
                         * Podemos obter esse Json genérico e convertê-lo para um
                         * objeto da classe nossa FunctionsGenericResponse
                         * e a partir dali, tratar o não a conversão do payload. Veja:
                         */

                        // convertendo.
                        val genericResp = gson.fromJson(task.result, FunctionsGenericResponse::class.java)


                        // abra a aba Logcat e selecione "INFO" e filtre por
                        Log.i(logEntry, genericResp.status.toString())
                        Log.i(logEntry, genericResp.message.toString())

                        // claro, o payload deve ser convertido para outra coisa depois.
                        Log.i(logEntry, genericResp.payload.toString())

                        /*
                            Converter o "payload" para um objeto mais específico para
                            tratar o docId. Veja a classe "InsertResult"
                            Lembrando que para cada situação o payload é um campo "polimorfico"
                            por isso na classe de resposta genérica é um Any.
                        */
                        val insertInfo = gson.fromJson(genericResp.payload.toString(), GenericInsertResponse::class.java)

                        Snackbar.make(binding.btnPlaca, "Placa cadastrada: " + insertInfo.docId,
                            Snackbar.LENGTH_LONG).show();
                        binding.etPlaca.text!!.clear()
                    }
                })
        }



        binding.btnOpenCamera.setOnClickListener {

            // solicitar permissao da Camera.
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
        // navegar para a outra activity.
        val intentSelecaoFoto = Intent(this,ActivitySelecaoFoto::class.java)
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
            // se faz necessario transformar a string de retorno como uma string Json valida.
            val res = gson.toJson(task.result?.data)
            res
        }
}
}