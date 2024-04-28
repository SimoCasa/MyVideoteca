package com.uninsubria.myvideoteca

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.uninsubria.myvideoteca.ui.dvd.CDItem

private lateinit var auth: FirebaseAuth
private var admin: Boolean = false
private lateinit var myRef : DatabaseReference
private lateinit var cdTitle: TextInputEditText
private lateinit var detailAuthor: EditText
private lateinit var detailCdDuration: EditText
private lateinit var detailRecords: EditText
private lateinit var detailGenre: EditText
private lateinit var detailTracks: EditText
private lateinit var selectedCD: CDItem
private lateinit var btnBook: MaterialButton
private lateinit var btnEdit: MaterialButton
private lateinit var btnRemove: MaterialButton

class DetailedCD : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.detailed_cd)

        // Ottieni i dati relativi all'elemento selezionato passati dall'intent
        selectedCD = intent.getSerializableExtra("selectedCD") as CDItem

        // Imposta i dettagli dell'elemento selezionato nei vari elementi del layout
        initElements()

        cdTitle.setText(selectedCD.title)
        detailAuthor.setText(selectedCD.autori)
        detailCdDuration.setText(selectedCD.durata)
        detailRecords.setText(selectedCD.casa_discografica)
        detailGenre.setText(selectedCD.genere)
        detailTracks.setText(selectedCD.track)


        // Aggiungi altre view per altri dettagli se necessario
        Log.d("Link Immagine",selectedCD.img)

        Glide.with(this)
            .load(selectedCD.img)
            .error(R.drawable.cd_disk)
            .into(findViewById(R.id.cdImg))
        Glide.with(this)
            .load(selectedCD.img)
            .error(R.drawable.cd_disk)
            .into(findViewById(R.id.cdBlurredImg))

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
                        cdTitle.isEnabled=true
                        detailAuthor.isEnabled=true
                        detailCdDuration.isEnabled=true
                        detailRecords.isEnabled=true
                        detailGenre.isEnabled=true
                        detailTracks.isEnabled=true
                        btnRemove.visibility = View.VISIBLE
                        btnEdit.visibility = View.VISIBLE
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })

        //Alla pressione del bottone MODIFICA
        btnEdit.setOnClickListener{
            if(admin){
                val newTitle = cdTitle.text.toString()
                val newAuthor = detailAuthor.text.toString()
                val newDuration = detailCdDuration.text.toString()
                val newRecords = detailRecords.text.toString()
                val newGenre = detailGenre.text.toString()
                val newTracks = detailTracks.text.toString()

                // Ottieni un riferimento al database
                val database = FirebaseDatabase.getInstance()

                // Ottieni un riferimento all'elemento che vuoi modificare
                myRef = database.getReference(selectedCD.ref)

                // Crea un oggetto con i dati che vuoi aggiornare
                val updatedData = HashMap<String, Any>()
                updatedData["autori"] = newAuthor
                updatedData["casa_discografica"] = newRecords
                updatedData["durata"] = newDuration
                updatedData["genere"] = newGenre
                updatedData["title"] = newTitle
                updatedData["track"] = newTracks

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
                    myRef = database.getReference(selectedCD.ref)
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

        //Imposto che se il titolo risulta già preso in prestito blocco il bottone
    }

    private fun initElements() {
        cdTitle=findViewById(R.id.cdTitle)
        detailAuthor=findViewById(R.id.detailAuthor)
        detailCdDuration=findViewById(R.id.detailCdDuration)
        detailRecords=findViewById(R.id.detailRecords)
        detailGenre=findViewById(R.id.detailGenre)
        detailTracks=findViewById(R.id.detailTracks)
        btnBook=findViewById(R.id.btnBook)
        btnEdit=findViewById(R.id.btnEdit)
        btnRemove=findViewById(R.id.btnRemove)
    }
}