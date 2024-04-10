package com.uninsubria.myvideoteca

import android.annotation.SuppressLint
import androidx.appcompat.app.AlertDialog
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.util.HashMap
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.progressindicator.CircularProgressIndicator

class RegisterActivity : AppCompatActivity(){
    // Creazione var per DB Firebase
    private lateinit var firebaseAuth : FirebaseAuth
    //Creazione dialogo progresso
    private lateinit var progressBar: CircularProgressIndicator
    private lateinit var loadingDialog: AlertDialog
    @SuppressLint("CutPasteId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        firebaseAuth = FirebaseAuth.getInstance() //inizializzazione dell'autenticazione a firebase

        //Dialogo processo durante la registrazione in Firebase
        progressBar = CircularProgressIndicator(this)
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

        // Mostra il dialogo di caricamento
        loadingDialog = createLoadingDialog("Creo l'account...")

        //creo utente in Firebase
        firebaseAuth.createUserWithEmailAndPassword(emailRegister,passwordRegister)
            .addOnSuccessListener {
                //Richiamo il metodo per inserire le informazioni dell'utente nel Database Dinamico di Firebase
                updateUser(name,surname,emailRegister,passwordRegister)
            }
            .addOnFailureListener {
                //progressDialog.dismiss()
                loadingDialog.dismiss()
                Toast.makeText(applicationContext, "Utente non creato", Toast.LENGTH_SHORT).show()
            }

    }
    //Inserisco le informazioni dell'utente nel Database Dinamico di Firebase
    private fun updateUser(name: String, surname: String, email: String, password: String){

        //mostro il dialogo di progresso
        loadingDialog.dismiss()
        loadingDialog = createLoadingDialog("Salvo le informazioni...")
        Handler().postDelayed({
            loadingDialog.dismiss()
        }, 110) // Ritardo di 1 secondo prima di chiudere il dialogo

        //prendo il tempo corrente da inserire
        val timestamp = System.currentTimeMillis()
        //prendo l'id dell'utente
        val uid = firebaseAuth.uid
        //preparo i dati da mettere nel db
        val hashMap = hashMapOf(
            "uid" to uid,
            "email" to email,
            "name" to name,
            "surname" to surname,
            "rule" to "user", // di base impostiamo tutti gli utenti 'User' per poi renderli 'Admin' da DB se necessario
            "timestamp" to timestamp
        )
        //inserisco in Firebase
        val ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.child(uid!!)
            .setValue(hashMap)
            .addOnSuccessListener {
                loadingDialog.dismiss()
                //account creato con successo e aggiunta informazioni utente nel DB
                val openHome = Intent(this, HomePage::class.java)
                // attivazione dell'activity di Login
                startActivity(openHome)
                finish()
            }
            .addOnFailureListener{
                loadingDialog.dismiss()
                Toast.makeText(applicationContext, "Utente non creato", Toast.LENGTH_SHORT).show()

            }
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
    //Creo dialogo di caricamento/attesa
    private fun createLoadingDialog(message: String): AlertDialog {
        val dialog = MaterialAlertDialogBuilder(this)
            .setMessage(message)
            .setCancelable(false)
            .create()
        dialog.show()
        return dialog
    }
}