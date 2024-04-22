package com.uninsubria.myvideoteca

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.uninsubria.myvideoteca.ui.blueray.BlueRayItem
import com.google.android.material.textview.MaterialTextView
import com.uninsubria.myvideoteca.ui.dvd.DVDItem

class DetailedBRDVD : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.detailed_brdvd)

        // Ottieni i dati relativi all'elemento selezionato passati dall'intent
        val intent = intent
        val callingActivity = intent.getStringExtra("callingActivity")

        if (callingActivity == "ActivityBlue") {
            val selectedBlueRay = intent.getSerializableExtra("selectedBlueRay") as BlueRayItem
            // Imposta i dettagli dell'elemento selezionato nei vari elementi del layout
            findViewById<MaterialTextView>(R.id.filmTitle).text = selectedBlueRay.title
            findViewById<MaterialTextView>(R.id.detailDirector).text = selectedBlueRay.regista
            findViewById<MaterialTextView>(R.id.detailDuration).text = selectedBlueRay.durata
            findViewById<MaterialTextView>(R.id.detailPlot).text = selectedBlueRay.trama
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

        } else if (callingActivity == "ActivityDVD") {
            val selectedDVD = intent.getSerializableExtra("selectedDVD") as DVDItem
            // Imposta i dettagli dell'elemento selezionato nei vari elementi del layout
            findViewById<MaterialTextView>(R.id.filmTitle).text = selectedDVD.title
            findViewById<MaterialTextView>(R.id.detailDirector).text = selectedDVD.regista
            findViewById<MaterialTextView>(R.id.detailDuration).text = selectedDVD.durata
            findViewById<MaterialTextView>(R.id.detailPlot).text = selectedDVD.trama
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

    }
}
