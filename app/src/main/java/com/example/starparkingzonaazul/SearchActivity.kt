package com.example.starparkingzonaazul

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.cardview.widget.CardView
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
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

class SearchActivity : AppCompatActivity() {
    private lateinit var etPlaca: TextInputEditText
    private lateinit var btnPesquisar: MaterialButton
    private lateinit var returnCard: CardView
    private lateinit var placaField: TextView
    private lateinit var condicaoField: TextView
    private lateinit var dataField: TextView
    private lateinit var responseField: TextView
    private lateinit var cancellButton: MaterialButton
    private lateinit var confirmButton: MaterialButton
    private lateinit var returnIrregular: CardView
    private val logEntry = "BUSCAR_PAGAMENTO"
    private lateinit var functions: FirebaseFunctions
    private val gson = GsonBuilder().enableComplexMapKeySerialization().create()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_StarparkingZonaAzul)
        setContentView(R.layout.activity_search)

        functions = Firebase.functions("southamerica-east1")
        etPlaca = findViewById(R.id.searchPlaca)
        btnPesquisar = findViewById(R.id.btnPesquisar)
        returnCard = findViewById(R.id.returnCard)
        placaField = findViewById(R.id.placaField)
        condicaoField = findViewById(R.id.condicaoField)
        dataField = findViewById(R.id.dataField)
        responseField = findViewById(R.id.responseField)
        returnIrregular = findViewById(R.id.returnIrregular)
        cancellButton = findViewById(R.id.cancellButton)
        confirmButton = findViewById(R.id.confirmButton)

        btnPesquisar.setOnClickListener {

            val data = DateTimeFormatter
                .ofPattern("dd/MM/yyyy - hh:mm:ss")
                .withZone(ZoneOffset.UTC)
                .format(LocalDateTime.now())
            val p = Pagamento(etPlaca.text.toString())
            checarPagamento(p)
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

                        val payload = genericResp.payload.toString()
                        if (payload == "Veículo irregular"){
                            closeKeyBoard()
                            returnCard.setVisibility(View.VISIBLE)
                            placaField.text = "Placa: ${etPlaca.text}"
                            condicaoField.text = "Condição: Veículo irregular"
                            dataField.text = "Data: $data"
                            responseField.text = "Veículo irregular, deseja registrar uma irregularidade?"
                            returnIrregular.setVisibility(View.VISIBLE)
                            confirmButton.setOnClickListener {
                                redirecionarIrregularidade()
                            }

                        } else if (payload == "Veículo regular"){
                            closeKeyBoard()
                            returnCard.setVisibility(View.VISIBLE)
                            placaField.text = "Placa: ${etPlaca.text}"
                            condicaoField.text = "Condição: Veículo regular"
                            dataField.text = "Data: $data"
                            returnIrregular.setVisibility(View.INVISIBLE)
                        }
                        else {
                            closeKeyBoard()
                            Snackbar.make(btnPesquisar,"Ocorreu um erro: ${payload}", Snackbar.LENGTH_LONG).show()
                            returnIrregular.setVisibility(View.INVISIBLE)
                            returnCard.setVisibility(View.INVISIBLE)
                        }

                    }
                })
        }
    }

    private fun checarPagamento(p: Pagamento): Task<String> {
        val data = hashMapOf(
            "placa" to p.placa,
        )
        return functions
            .getHttpsCallable("ChecarStatus")
            .call(data)
            .continueWith { task ->
                val res = gson.toJson(task.result?.data)
                res
            }
    }

    private fun redirecionarIrregularidade() {
        val intentIrregularidade = Intent(this, RegistrarIrregularidadeActivity::class.java)
        startActivity(intentIrregularidade)
    }

    private fun closeKeyBoard() {
        val view = this.currentFocus
        if (view != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }
}

