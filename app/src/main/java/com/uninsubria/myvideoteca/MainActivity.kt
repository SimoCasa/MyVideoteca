package com.uninsubria.myvideoteca

import android.app.ProgressDialog
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.firebase.auth.FirebaseAuth


class MainActivity : AppCompatActivity() {
    // Creazione var per DB Firebase
    private lateinit var auth: FirebaseAuth
    //Creazione dialogo progresso
    private lateinit var progressBar: CircularProgressIndicator
    private lateinit var loadingDialog: AlertDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()

        //Dialogo processo durante la registrazione in Firebase
        progressBar = CircularProgressIndicator(this)
        //...

        setContentView(R.layout.activity_login)
        val emailLogin = findViewById<EditText>(R.id.emailField)
        val passwordLogin = findViewById<EditText>(R.id.passwordField)

        //Controllo su casella email
        emailLogin.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val email = emailLogin.text.toString().trim()
                //Controllo validità indirizzo email
                if (!isValidEmail(email)) {
                    emailLogin.error = "Indirizzo email non valido"
                }else{
                    emailLogin.error = null  // Rimuove il messaggio di errore, se presente
                    emailLogin.backgroundTintList = ColorStateList.valueOf(Color.GREEN) // Imposta il colore del bordo a verde
                }
            }
        }

        //Bottone per la Registrazione
        val regButton = findViewById<Button>(R.id.registerButton)
        regButton.setOnClickListener{
            val openReg = Intent(this, RegisterActivity::class.java)
            // attivazione dell'activity di Login
            startActivity(openReg)
        }

        //Bottone per la Login
        val logButton = findViewById<Button>(R.id.loginButton)
        logButton.setOnClickListener{
            val email = emailLogin.text.toString()
            val password = passwordLogin.text.toString()
            if(formatChecks(email,password)){
                login(email,password)
            }else{
                Toast.makeText(applicationContext, "I dati inseriti non sono validi", Toast.LENGTH_SHORT).show()
            }
        }
    }

    //LOGIN TRAMITE DB che se avvenuto con successo apre un'Intent sulla HOMEPAGE altrimenti manda un messaggio TOAST
    private fun login(email: String, password: String){
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                // Mostro il progress durante il login
                loadingDialog = createLoadingDialog("Accesso all'account...")
                if (task.isSuccessful) { // Login riuscito
                    Handler().postDelayed({
                        loadingDialog.dismiss()
                    }, 110) // Ritardo di 1 secondo prima di chiudere il dialogo
                    val openHome = Intent(this, HomePage::class.java)
                    // attivazione dell'activity di Login
                    startActivity(openHome)
                    //loadingDialog.dismiss()
                } else {
                    loadingDialog.dismiss()
                    // Login fallito
                    Toast.makeText(this, "Login fallito. Verifica email e password", Toast.LENGTH_SHORT).show()
                }
            }
    }

    //Controllo che i campi non siano vuoti
    private fun formatChecks(emailLogin: String, passwordLogin: String): Boolean {
        return emailLogin.isNotBlank() && passwordLogin.isNotBlank() && isValidEmail(emailLogin)
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