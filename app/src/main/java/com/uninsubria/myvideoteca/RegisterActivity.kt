package com.uninsubria.myvideoteca

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class RegisterActivity : AppCompatActivity(){
    // Creazione var per DB Firebase
    private lateinit var firebaseAuth : FirebaseAuth
    // Creazione var per dialogo progresso
    private lateinit var progressDialog : ProgressDialog
    @SuppressLint("CutPasteId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        firebaseAuth = FirebaseAuth.getInstance() //inizializzazione dell'autenticazione a firebase

        //Dialogo processo durante la registrazione in Firebase
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Attendere...")
        progressDialog.setCanceledOnTouchOutside(false)
        //...
        val name = findViewById<EditText>(R.id.nameField) as EditText
        val surname = findViewById<EditText>(R.id.surnameField) as EditText
        val emailRegister = findViewById<EditText>(R.id.emailField) as EditText
        val passwordRegister = findViewById<EditText>(R.id.passwordField) as EditText
        val regButton = findViewById<Button>(R.id.registerButton) as Button

        //Controllo sulla mail
        emailRegister.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val email = emailRegister.text.toString().trim()
                //Controllo validità indirizzo email
                if (!isValidEmail(email)) {
                    emailRegister.error = "Indirizzo email non valido"
                }else{
                    emailRegister.error = null  // Rimuove il messaggio di errore, se presente
                    emailRegister.backgroundTintList = ColorStateList.valueOf(Color.GREEN) // Imposta il colore del bordo a verde
                }
            }
        }

        //Pressione sul bottone registrati
        regButton.setOnClickListener{
            val name = name.text.toString()
            val surname = surname.text.toString()
            val email = emailRegister.text.toString()
            val password = passwordRegister.text.toString()

            if(formatChecks(name,surname,email,password)){
                register(name,surname,email,password) //esegue la registrazione
            }else{
                Toast.makeText(applicationContext, "I dati inseriti non sono validi", Toast.LENGTH_SHORT).show()
            }
        }
    }

    //AGGIUNGO TUTTE LE VARIABILI AL DB, SE NON SI CONNETTE O ALTRO NON DEVO ANDARE AVANTI ALLA HOME
    private fun register(name: String, surname: String, emailRegister: String, passwordRegister: String) {
        //mostro il messaggio di progresso
        progressDialog.setMessage("Creo l'account...")
        progressDialog.show()
        //creo utente in Firebase
        firebaseAuth.createUserWithEmailAndPassword(emailRegister,passwordRegister)
            .addOnSuccessListener {
                //account creato con successo e aggiunta informazioni utente nel DB
                val openHome = Intent(this, HomePage::class.java)
                // attivazione dell'activity di Login
                startActivity(openHome)
                //45:43
            }
            .addOnFailureListener {e->
                progressDialog.dismiss()
                Toast.makeText(applicationContext, "Utente non creato", Toast.LENGTH_SHORT).show()
            }
        //continuo

    }

    //Controllo che i campi non siano vuoti
    private fun formatChecks(name: String, surname: String, emailRegister: String, passwordRegister: String): Boolean {
        return name.isNotBlank() && surname.isNotBlank() && emailRegister.isNotBlank() && passwordRegister.isNotBlank() && isValidEmail(emailRegister)
    }

    //Controllo che la mail sia formattata correttamente
    private fun isValidEmail(email: String): Boolean {
        val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
        return email.matches(emailPattern.toRegex())
    }
}