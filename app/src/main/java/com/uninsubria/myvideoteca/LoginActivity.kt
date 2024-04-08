package com.uninsubria.myvideoteca

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {
    private var check=false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val regButton = findViewById<Button>(R.id.registerButton)

        regButton.setOnClickListener{
            val openReg = Intent(this, RegisterActivity::class.java)
            // attivazione dell'activity di Login
            startActivity(openReg)
        }

        //Controllo su casella email
        val emailEditText = findViewById<EditText>(R.id.emailField)
        emailEditText.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val email = emailEditText.text.toString().trim()
                //Controllo validità indirizzo email
                if (!isValidEmail(email)) {
                    emailEditText.error = "Indirizzo email non valido"
                    check = false
                }else{
                    emailEditText.error = null  // Rimuove il messaggio di errore, se presente
                    emailEditText.backgroundTintList = ColorStateList.valueOf(Color.GREEN) // Imposta il colore del bordo a verde
                    check=true
                }
            }
        }

    }

    private fun isValidEmail(email: String): Boolean {
        val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
        return email.matches(emailPattern.toRegex())
    }
}