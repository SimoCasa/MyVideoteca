package com.uninsubria.myvideoteca

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.android.material.textview.MaterialTextView
import com.uninsubria.myvideoteca.ui.dvd.CDItem

class DetailedCD : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.detailed_cd)

        // Ottieni i dati relativi all'elemento selezionato passati dall'intent
        val selectedCD = intent.getSerializableExtra("selectedCD") as CDItem

        // Imposta i dettagli dell'elemento selezionato nei vari elementi del layout
        findViewById<MaterialTextView>(R.id.cdTitle).text = selectedCD.title
        findViewById<MaterialTextView>(R.id.detailAuthor).text = selectedCD.autori
        findViewById<MaterialTextView>(R.id.detailCdDuration).text = selectedCD.durata
        findViewById<MaterialTextView>(R.id.detailRecords).text = selectedCD.casa_discografica
        findViewById<MaterialTextView>(R.id.detailGenre).text = selectedCD.genere


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
        //Imposto che se il titolo risulta già preso in prestito blocco il bottone
    }
}