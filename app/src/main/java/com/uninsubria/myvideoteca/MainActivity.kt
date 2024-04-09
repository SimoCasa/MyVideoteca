package com.uninsubria.myvideoteca

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {
    private var check=false
    private lateinit var appDb : AppDatabase
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
        setContentView(R.layout.activity_login)
        appDb = AppDatabase.getDatabase(this)
        addSampleUsers()
        val emailLogin = findViewById<EditText>(R.id.emailField)
        val passwordLogin = findViewById<EditText>(R.id.passwordField)

        //Controllo su casella email
        emailLogin.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val email = emailLogin.text.toString().trim()
                //Controllo validità indirizzo email
                if (!isValidEmail(email)) {
                    emailLogin.error = "Indirizzo email non valido"
                    check = false
                }else{
                    emailLogin.error = null  // Rimuove il messaggio di errore, se presente
                    emailLogin.backgroundTintList = ColorStateList.valueOf(Color.GREEN) // Imposta il colore del bordo a verde
                    check=true
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
                if (task.isSuccessful) {
                    // Login riuscito
                    Toast.makeText(this, "Login riuscito", Toast.LENGTH_SHORT).show()
                    // Puoi aggiungere qui il codice per passare alla schermata successiva
                } else {
                    // Login fallito
                    Toast.makeText(this, "Login fallito. Verifica email e password", Toast.LENGTH_SHORT).show()
                }
            }
    }

    //Controllo che i campi non siano vuoti (l'email è controllata dalla variabile check)
    private fun formatChecks(emailLogin: String, passwordLogin: String): Boolean {
        return emailLogin.isNotBlank() && passwordLogin.isNotBlank() && check
    }

    //Controllo che la mail sia formattata correttamente
    private fun isValidEmail(email: String): Boolean {
        val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
        return email.matches(emailPattern.toRegex())
    }
    private fun addSampleUsers() {

        val utente1 = User (
            null, "Alberto", "prova"
        )
        GlobalScope.launch(Dispatchers.IO) {
            val existingUser = appDb.userDao().getUser(utente1.username.toString(), utente1.password.toString())
            if (existingUser == null) {
                appDb.userDao().insertUser(utente1)
            }
        }
    }
}