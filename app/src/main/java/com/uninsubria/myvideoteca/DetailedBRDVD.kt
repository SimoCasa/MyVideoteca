package com.uninsubria.myvideoteca

import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.uninsubria.myvideoteca.ui.blueray.BlueRayItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.uninsubria.myvideoteca.ui.dvd.DVDItem

class DetailedBRDVD : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var filmTitle: TextInputEditText
    private lateinit var detailDirector: EditText
    private lateinit var detailDuration: EditText
    private lateinit var detailPlot: EditText
    private lateinit var btnBook: MaterialButton
    private lateinit var btnEdit: MaterialButton
    private lateinit var btnRemove: MaterialButton
    private lateinit var selectedBlueRay: BlueRayItem
    private lateinit var selectedDVD: DVDItem
    private lateinit var Available: TextView
    private lateinit var myRef : DatabaseReference
    private var admin: Boolean = false
    private lateinit var disk : String
    private lateinit var callingActivity: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.detailed_brdvd)

        // Ottieni i dati relativi all'elemento selezionato passati dall'intent
        val intent = intent
        callingActivity = intent.getStringExtra("callingActivity").toString()

        initElements()

        //SE ARRIVA DA BLUERAY
        if (callingActivity == "ActivityBlue") {
            disk="BR"
            selectedBlueRay = intent.getSerializableExtra("selectedBlueRay") as BlueRayItem
            // Imposta i dettagli dell'elemento selezionato nei vari elementi del layout
            filmTitle.setText(selectedBlueRay.title)
            detailDirector.setText(selectedBlueRay.regista)
            detailDuration.setText(selectedBlueRay.durata)
            detailPlot.setText(selectedBlueRay.trama)
            // Verifico se il prodotto è disponibile da DB
            if(selectedBlueRay.available == true){
                val fullText = "Disponibilità: SÌ"
                val spannableString = SpannableString(fullText)
                // Definisci il colore verde per il testo
                val greenColor = ContextCompat.getColor(this, R.color.dark_green)
                //Abilitato il tasto
                btnBook.isEnabled = true
                // Applica lo stile Span
                spannableString.setSpan(
                    ForegroundColorSpan(greenColor),
                    fullText.indexOf("SÌ"),
                    fullText.indexOf("SÌ") + "SÌ".length,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )

                Available.text = spannableString
            }
            else if (selectedBlueRay.available == false){
                val fullText = "Disponibilità: NO"
                val spannableString = SpannableString(fullText)
                // Definisci il colore verde per il testo
                val redColor = ContextCompat.getColor(this, R.color.red)
                //Disabilito il tasto
                btnBook.isEnabled = false
                // Applica lo stile Span
                spannableString.setSpan(
                    ForegroundColorSpan(redColor),
                    fullText.indexOf("NO"),
                    fullText.indexOf("NO") + "NO".length,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                Available.text = spannableString
            }

            // Aggiungi altre view per altri dettagli se necessario
            Log.d("Link Immagine",selectedBlueRay.locandina)

            Glide.with(this)
                .load(selectedBlueRay.locandina)
                .error(R.drawable.br_disk)
                .into(findViewById(R.id.filmImg))
            Glide.with(this)
                .load(selectedBlueRay.locandina)
                .error(R.drawable.br_disk)
                .into(findViewById(R.id.filmBlurredImg))
            //Imposto che se il titolo risulta già preso in prestito blocco il bottone

        //SE ARRIVA DA DVD
        } else if (callingActivity == "ActivityDVD") {
            disk="DVD"
            selectedDVD = intent.getSerializableExtra("selectedDVD") as DVDItem
            // Imposta i dettagli dell'elemento selezionato nei vari elementi del layout
            filmTitle.setText(selectedDVD.title)
            detailDirector.setText(selectedDVD.regista)
            detailDuration.setText(selectedDVD.durata)
            detailPlot.setText(selectedDVD.trama)
            // Verifico se il prodotto è disponibile da DB
            if(selectedDVD.available == true){
                val fullText = "Disponibilità: SÌ"
                val spannableString = SpannableString(fullText)
                // Definisci il colore verde per il testo
                val greenColor = ContextCompat.getColor(this, R.color.dark_green)
                //Abilitato il tasto
                btnBook.isEnabled = true
                // Applica lo stile Span
                spannableString.setSpan(
                    ForegroundColorSpan(greenColor),
                    fullText.indexOf("SÌ"),
                    fullText.indexOf("SÌ") + "SÌ".length,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )

                Available.text = spannableString
            }
            else if (selectedDVD.available == false){
                val fullText = "Disponibilità: NO"
                val spannableString = SpannableString(fullText)
                // Definisci il colore verde per il testo
                val redColor = ContextCompat.getColor(this, R.color.red)
                //Disabilito il tasto
                btnBook.isEnabled = false
                // Applica lo stile Span
                spannableString.setSpan(
                    ForegroundColorSpan(redColor),
                    fullText.indexOf("NO"),
                    fullText.indexOf("NO") + "NO".length,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                Available.text = spannableString
            }

            // Aggiungi altre view per altri dettagli se necessario
            Log.d("Link Immagine",selectedDVD.locandina)

            Glide.with(this)
                .load(selectedDVD.locandina)
                .error(R.drawable.br_disk)
                .into(findViewById(R.id.filmImg))
            Glide.with(this)
                .load(selectedDVD.locandina)
                .error(R.drawable.dvd_disk)
                .into(findViewById(R.id.filmBlurredImg))

            //Imposto che se il titolo risulta già preso in prestito blocco il bottone
        }

        //Prendo istanza
        auth = FirebaseAuth.getInstance()
        val firebaseUser = auth.currentUser!!
        val ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.child(firebaseUser.uid)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val rule = snapshot.child("rule").value
                    if(rule=="admin"){
                        admin=true  //imposta se l'utente è veramente un admin
                        filmTitle.isEnabled=true
                        detailDirector.isEnabled=true
                        detailDuration.isEnabled=true
                        detailPlot.isEnabled=true
                        btnRemove.visibility = View.VISIBLE
                        btnEdit.visibility = View.VISIBLE
                        btnBook.isEnabled = false
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })

        //Alla pressione del bottone MODIFICA
        btnEdit.setOnClickListener{
            if(admin){
                val newTitle = filmTitle.text.toString()
                val newDirector = detailDirector.text.toString()
                val newDuration = detailDuration.text.toString()
                val newPlot = detailPlot.text.toString()

                // Ottieni un riferimento al database
                val database = FirebaseDatabase.getInstance()


                // Ottieni un riferimento all'elemento che vuoi modificare
                if(disk=="BR"){ myRef = database.getReference(selectedBlueRay.ref)}
                else if(disk=="DVD"){ myRef = database.getReference(selectedDVD.ref)}

                // Crea un oggetto con i dati che vuoi aggiornare
                val updatedData = HashMap<String, Any>()
                updatedData["durata"] = newDuration
                updatedData["regista"] = newDirector
                updatedData["title"] = newTitle
                updatedData["trama"] = newPlot

                // Aggiorna l'elemento
                myRef.updateChildren(updatedData).addOnSuccessListener {
                    // L'aggiornamento è stato completato con successo, avvia un'altra Activity
                    val intent = Intent(this, HomePage::class.java)
                    startActivity(intent)
                    Toast.makeText(this, "Modifica effettuata con successo", Toast.LENGTH_SHORT).show()
                }.addOnFailureListener {
                    // L'aggiornamento è fallito, mostra un messaggio Toast
                    Toast.makeText(this, "Modifica non convalidata", Toast.LENGTH_SHORT).show()
                }
            }
        }
        //Alla pressione del bottone PRENOTA
        btnBook.setOnClickListener{
            // Recupero l'ID dell'utente corrente
            val currentUser = FirebaseAuth.getInstance().currentUser
            if (currentUser != null) {
                val currentUserId = currentUser.uid
                if(callingActivity == "ActivityBlue"){
                    if(selectedBlueRay.available == true){
                        val database = FirebaseDatabase.getInstance()

                        // Ottieni un riferimento all'elemento che vuoi modificare
                        if(disk=="BR"){ myRef = database.getReference(selectedBlueRay.ref)}
                        else if(disk=="DVD"){ myRef = database.getReference(selectedDVD.ref)}

                        // Crea un oggetto con i dati che vuoi aggiornare
                        val updatedData = HashMap<String, Any>()
                        updatedData["available"] = false
                        updatedData["userId"] = currentUserId

                        // Aggiorna l'elemento
                        myRef.updateChildren(updatedData).addOnSuccessListener {
                            // L'aggiornamento è stato completato con successo, avvia un'altra Activity
                            val intent = Intent(this, HomePage::class.java)
                            startActivity(intent)
                            Toast.makeText(this, "Blu-ray prenotato con successo!", Toast.LENGTH_SHORT).show()
                        }.addOnFailureListener {
                            Toast.makeText(this, "Errore durante la prenotazione del Blu-ray.", Toast.LENGTH_SHORT).show()
                        }
                    }
                    else if (selectedBlueRay.available== false){
                        // Blu-ray non è disponibile
                        Toast.makeText(this, "Questo Blu-ray non è attualmente disponibile per la prenotazione", Toast.LENGTH_LONG).show()
                    }
                }
                else if (callingActivity == "ActivityDVD"){
                    if(selectedDVD.available == true){
                        // DVD è disponibile, quindi procedo con la prenotazione
                        val database = FirebaseDatabase.getInstance()

                        // Ottieni un riferimento all'elemento che vuoi modificare
                        if(disk=="BR"){ myRef = database.getReference(selectedBlueRay.ref)}
                        else if(disk=="DVD"){ myRef = database.getReference(selectedDVD.ref)}

                        // Crea un oggetto con i dati che vuoi aggiornare
                        val updatedData = HashMap<String, Any>()
                        updatedData["available"] = false
                        updatedData["userId"] = currentUserId

                        // Aggiorna l'elemento
                        myRef.updateChildren(updatedData).addOnSuccessListener {
                            // L'aggiornamento è stato completato con successo, avvia un'altra Activity
                            val intent = Intent(this, HomePage::class.java)
                            startActivity(intent)
                            Toast.makeText(this, "DVD prenotato con successo!", Toast.LENGTH_SHORT).show()
                        }.addOnFailureListener {
                            Toast.makeText(this, "Errore durante la prenotazione del DVD", Toast.LENGTH_SHORT).show()
                        }
                    }
                    else if (selectedDVD.available == false){
                        // DVD non è disponibile
                        Toast.makeText(this, "Questo DVD non è attualmente disponibile per la prenotazione", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }

        //Alla pressione del bottone RIMUOVI
        btnRemove.setOnClickListener{
            if(admin){
                // Crea un AlertDialog
                val builder = AlertDialog.Builder(this)
                builder.setTitle("Conferma")
                builder.setMessage("Sei sicuro di rimuovere definitivamente l'elemento?")

                // Imposta il pulsante "Sì"
                builder.setPositiveButton("Sì", null)

                // Imposta il pulsante "No"
                builder.setNegativeButton("No", null)

                // Mostra l'AlertDialog
                val dialog = builder.show()

                // Cambia il colore del testo del pulsante "Sì"
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(this, R.color.my_secondary))

                // Cambia il colore del testo del pulsante "No"
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(this, R.color.my_secondary))

                // Imposta l'azione del pulsante "Sì"
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                    // Ottieni un riferimento al database
                    val database = FirebaseDatabase.getInstance()
                    // Ottieni un riferimento all'elemento che vuoi modificare
                    if(disk=="BR"){ myRef = database.getReference(selectedBlueRay.ref)}
                    else if(disk=="DVD"){ myRef = database.getReference(selectedDVD.ref)}
                    // Elimina l'oggetto
                    myRef.removeValue().addOnSuccessListener {
                        // L'eliminazione è stata completata con successo
                        val intent = Intent(this, HomePage::class.java)
                        startActivity(intent)
                        Toast.makeText(this, "Oggetto eliminato con successo", Toast.LENGTH_SHORT).show()
                        dialog.dismiss()
                    }.addOnFailureListener {
                        // L'eliminazione è fallita
                        Toast.makeText(this, "Eliminazione non riuscita", Toast.LENGTH_SHORT).show()
                    }
                }
                // Imposta l'azione del pulsante "No"
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener {
                    // Chiudi il dialogo
                    dialog.dismiss()
                }
            }
        }


    }

    //Assegnazione delle variabili agli elementi
    private fun initElements() {
        filmTitle=findViewById<TextInputEditText>(R.id.filmTitle)
        detailDirector=findViewById<EditText>(R.id.detailDirector)
        detailDuration=findViewById<EditText>(R.id.detailDuration)
        detailPlot=findViewById<EditText>(R.id.detailPlot)
        btnBook=findViewById<MaterialButton>(R.id.btnBook)
        btnEdit=findViewById<MaterialButton>(R.id.btnEdit)
        btnRemove=findViewById<MaterialButton>(R.id.btnRemove)
        Available = findViewById<TextView>(R.id.AvailableState)
    }
}
