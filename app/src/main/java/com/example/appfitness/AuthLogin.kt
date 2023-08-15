package com.example.appfitness

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.analytics.FirebaseAnalytics
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AuthLogin : AppCompatActivity() {

    private lateinit var btn_login: Button
    private lateinit var btn_register: Button
    private lateinit var edittext_email: EditText
    private lateinit var edittext_nombre: EditText
    private lateinit var edittext_passw: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.authlogin)

        //Declaraciones de las referencias por ID

        btn_login = findViewById(R.id.btn_login)
        btn_register = findViewById(R.id.btn_registrar)
        edittext_email = findViewById(R.id.edit_text_email)
        edittext_nombre = findViewById(R.id.edit_text_nombre)
        edittext_passw = findViewById(R.id.edit_text_passw)

        //PRUEBA DE EVENTOS
        val analytics:FirebaseAnalytics = FirebaseAnalytics.getInstance(this)
        val bundle = Bundle()
        bundle.putString("message", "Integracion a Firebase Completada")
        analytics.logEvent("InitScreen",bundle)

        //Start
        Start()

    }

    private fun Start() {
        title = "Autenticacion"

        btn_register.setOnClickListener {

            if (edittext_email.text.isNotEmpty() && edittext_passw.text.isNotEmpty()) {

                FirebaseAuth.getInstance().createUserWithEmailAndPassword(
                    edittext_email.text.toString(),
                    edittext_passw.text.toString()
                ).addOnCompleteListener {
                    if (it.isSuccessful) {
                        val user = FirebaseAuth.getInstance().currentUser
                        //ShowAlertSucces()
                        val name = edittext_nombre.text.toString()
                        val userData = hashMapOf(
                            "email" to (user?.email ?: ""),
                            "name" to name,
                            "passw" to edittext_passw.text.toString()
                            // Agrega más campos según tus necesidades
                        )
                        //ShowHome(it.result?.user?.email ?: "", ProviderType.BASIC)
                        // Guardar los datos en Firestore
                        if (user != null) {
                            FirebaseFirestore.getInstance().collection("usuarios")
                                .document(user.uid)
                                .set(userData)
                                .addOnSuccessListener {
                                    // Datos guardados exitosamente
                                    ShowAlertSucces()
                                    ShowHome(user.email ?: "", ProviderType.BASIC)
                                }
                                .addOnFailureListener { e ->
                                    // Error al guardar los datos
                                    ShowAlertErrorSave()
                                    Log.e("FirestoreError", "Error al guardar datos: ${e.message}")

                                }
                        }
                    }
                }
                    } else {
                        ShowAlertError()
                    }
                }






        btn_login.setOnClickListener {
            if (edittext_email.text.isNotEmpty() && edittext_passw.text.isNotEmpty()) {

                FirebaseAuth.getInstance().signInWithEmailAndPassword(
                    edittext_email.text.toString(),
                    edittext_passw.text.toString()
                ).addOnCompleteListener {
                    if (it.isSuccessful) {
                        ShowAlertSucces()
                        ShowHome(it.result?.user?.email ?: "", ProviderType.BASIC)
                    } else {
                        ShowAlertError()
                    }
                }
            }


        }
    }
        private fun ShowAlertError() {

            val builder = AlertDialog.Builder(this)
            builder.setTitle("Error")
            builder.setMessage("Se ha producido un error de autenticación al usuario")
            builder.setPositiveButton("Aceptar", null)
            val dialog: AlertDialog = builder.create()
            dialog.show()
        }


    private fun ShowAlertErrorSave() {

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage("Se ha producido un error al guardar los datos")
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

        private fun ShowAlertSucces() {

            val builder = AlertDialog.Builder(this)
            builder.setTitle("Succes")
            builder.setMessage("Ha funcionado todo perfecto")
            builder.setPositiveButton("Aceptar", null)
            val dialog: AlertDialog = builder.create()
            dialog.show()
        }


        private fun ShowHome(email: String, provider: ProviderType) {

            val IntentoMain: Intent = Intent(this, MainActivity::class.java).apply {
                putExtra("email", email)
                putExtra("provider", provider.name)
            }

            startActivity(IntentoMain)
        }



    }




