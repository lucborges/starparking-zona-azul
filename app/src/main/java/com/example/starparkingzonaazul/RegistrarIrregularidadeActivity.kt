package com.example.starparkingzonaazul

import android.app.Activity
import android.content.ClipData
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.shapes.Shape
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.starparkingzonaazul.databinding.ActivityRegistrarIrregularidadeBinding
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.android.material.button.MaterialButton
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.radiobutton.MaterialRadioButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.FirebaseFunctionsException
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.google.gson.GsonBuilder
import java.io.ByteArrayOutputStream
import java.net.URI
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

class RegistrarIrregularidadeActivity : AppCompatActivity() {

    private lateinit var etPlaca: TextInputEditText
    private lateinit var btnConfirmar: MaterialButton
    private lateinit var btnTempoExcedido: MaterialRadioButton
    private lateinit var btnLocalProibido: MaterialRadioButton
    private lateinit var imagemAtual: ShapeableImageView
    private lateinit var imgFrente: ShapeableImageView
    private lateinit var imgTras: ShapeableImageView
    private lateinit var imgDireita: ShapeableImageView
    private lateinit var imgEsquerda: ShapeableImageView

    private val logEntry = "CADASTRO_PLACA"
    private lateinit var functions: FirebaseFunctions
    private val gson = GsonBuilder().enableComplexMapKeySerialization().create()
    private lateinit var binding: ActivityRegistrarIrregularidadeBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registrar_irregularidade)
        binding = ActivityRegistrarIrregularidadeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        imgFrente = findViewById(R.id.imgFrente)
        imgTras = findViewById(R.id.imgTras)
        imgDireita = findViewById(R.id.imgLadoDireito)
        imgEsquerda = findViewById(R.id.imgLadoEsquerdo)


        binding.imgFrente.setOnClickListener{
            imagemAtual = binding.imgFrente
            cameraProviderResult.launch(android.Manifest.permission.CAMERA)
            cameraProviderResult.launch(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
            abrirTelaPreview()
            startForResult.launch(Intent(this, CameraPreviewActivity::class.java))

        }
        binding.imgLadoDireito.setOnClickListener{
            cameraProviderResult.launch(android.Manifest.permission.CAMERA)
            cameraProviderResult.launch(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
            abrirTelaPreview()
            imagemAtual = binding.imgLadoDireito
            startForResult.launch(Intent(this, CameraPreviewActivity::class.java))
        }
        binding.imgLadoEsquerdo.setOnClickListener{
            cameraProviderResult.launch(android.Manifest.permission.CAMERA)
            cameraProviderResult.launch(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
            abrirTelaPreview()
            imagemAtual = binding.imgLadoEsquerdo
            startForResult.launch(Intent(this, CameraPreviewActivity::class.java))
        }
        binding.imgTras.setOnClickListener{
            cameraProviderResult.launch(android.Manifest.permission.CAMERA)
            cameraProviderResult.launch(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
            abrirTelaPreview()
            imagemAtual = binding.imgTras
            startForResult.launch(Intent(this, CameraPreviewActivity::class.java))
        }

        functions = Firebase.functions("southamerica-east1")
        etPlaca = findViewById(R.id.etPlaca)
        btnConfirmar = findViewById(R.id.btnConfirmar)
        btnTempoExcedido =  findViewById(R.id.radio_button1)
        btnLocalProibido = findViewById(R.id.radio_button2)


        btnConfirmar.setOnClickListener {
            val p = Placa(etPlaca.text.toString())
            val data = DateTimeFormatter
                .ofPattern("dd-MM-yyyy")
                .withZone(ZoneOffset.UTC)
                .format(LocalDateTime.now())
            val tipoIrregularidade = if (btnTempoExcedido.isChecked) {
                "Tempo excedido."
            } else {
                "Veículo em local proibido."
            }

            cadastrarPlaca(p, data, tipoIrregularidade)
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
            uploadImage()
        }
    }

    private val cameraProviderResult =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            if (it) {
                CameraPreviewActivity()
            }
        }

    private fun abrirTelaPreview() {
        val intentCameraPreview = Intent(this, CameraPreviewActivity::class.java)
        startActivity(intentCameraPreview)
    }

    private val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result: ActivityResult ->
        if(result.resultCode == Activity.RESULT_OK){
                val uriImage = result.data?.data
                imagemAtual.setImageURI(uriImage)
            }
    }


    private fun uploadImage() {
        var count = 0
        val pics = arrayOfNulls<ShapeableImageView?>(4)

        pics[0] = imgFrente
        pics[1] = imgTras
        pics[2] = imgDireita
        pics[3] = imgEsquerda

        for(img in pics) {
            val storage = Firebase.storage
            val storageRef =
                storage.reference.child("imagens/img" + etPlaca.text.toString() + "(${count})" + ".jpg")

            val bitmap = (pics[count]?.drawable as BitmapDrawable).bitmap
            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos)
            val data = baos.toByteArray()

            val uploadTask = storageRef.putBytes(data)
            count++
            uploadTask.addOnFailureListener {
                Toast.makeText(
                    applicationContext, "Ocorreu um erro ao enviar a imagem.",
                    Toast.LENGTH_SHORT
                ).show()
            }.addOnSuccessListener { taskSnapshot ->
                Toast.makeText(
                    applicationContext, "Imagem enviada com sucesso.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }


    private fun cadastrarPlaca(p: Placa, data: String, tipoIrregularidade: String): Task<String> {
        val data = hashMapOf(
            "placa" to p.placa,
            "data" to data,
            "tipoIrregularidade" to tipoIrregularidade,
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