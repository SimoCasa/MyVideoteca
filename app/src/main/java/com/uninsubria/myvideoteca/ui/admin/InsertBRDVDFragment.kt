package com.uninsubria.myvideoteca.ui.admin

import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.uninsubria.myvideoteca.R
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import androidx.appcompat.app.AlertDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [InsertBRDVDFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class InsertBRDVDFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    //Creazione dialogo progresso
    private lateinit var loadingDialog: AlertDialog
    // Creazione var per DB Firebase
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_insert_brdvd, container, false)

        //Recupero sessione Firebase
        auth = FirebaseAuth.getInstance()

        // Recupera riferimenti agli elementi UI
        val radioButtonBlueRay = view.findViewById<RadioButton>(R.id.radioButtonBlueRay)
        val radioButtonDVD = view.findViewById<RadioButton>(R.id.radioButtonDVD)
        val editTextTitle = view.findViewById<EditText>(R.id.editTextTitle)
        val editTextRegista = view.findViewById<EditText>(R.id.editTextRegista)
        val editTextTrama = view.findViewById<EditText>(R.id.editTextTrama)
        val editTextHours = view.findViewById<EditText>(R.id.editTextHours)
        val editTextMinutes = view.findViewById<EditText>(R.id.editTextMinutes)
        val editTextSeconds = view.findViewById<EditText>(R.id.editTextSeconds)
        val btnAdd = view.findViewById<Button>(R.id.btnAdd)

        // Click listener per il bottone btnAdd
        btnAdd.setOnClickListener {
            // Recupera il tipo di media selezionato dall'utente
            val mediaType = when {
                radioButtonBlueRay.isChecked -> "Blu-ray"
                radioButtonDVD.isChecked -> "DVD"
                else -> ""
            }

            // Recupera i valori inseriti dall'utente
            val title = editTextTitle.text.toString()
            val regista = editTextRegista.text.toString()
            val trama = editTextTrama.text.toString()
            val hours = editTextHours.text.toString().toIntOrNull() ?: 0
            val minutes = editTextMinutes.text.toString().toIntOrNull() ?: 0
            val seconds = editTextSeconds.text.toString().toIntOrNull() ?: 0
            val durata = "$hours:$minutes:$seconds"

            // Esegui le operazioni desiderate con i valori recuperati
            // Ad esempio, puoi passare i valori ad un'altra funzione per aggiungerli al database
            // o puoi eseguire altre operazioni di business logic qui

            // Mostro il progress durante l'inserimento
            loadingDialog = createLoadingDialog("Inserisco i dati...")

            // Esempio: stampo i valori recuperati su console
            /*println("Media Type: $mediaType")
            println("Title: $title")
            println("Regista: $regista")
            println("Trama: $trama")
            println("Durata: $hours:$minutes:$seconds")
            */

            // Inserisci il film nel database
            inserisciFilm(mediaType, title, regista, trama, durata)

            // Termino il progress durante l'inserimento
            Handler().postDelayed({
                loadingDialog.dismiss()
            }, 1000) // Ritardo di 1 secondo prima di chiudere il dialogo

        }

        // Inflate the layout for this fragment
        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment InsertFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            InsertBRDVDFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
    private fun createLoadingDialog(message: String): AlertDialog {
        return MaterialAlertDialogBuilder(requireContext())
            .setMessage(message)
            .setCancelable(false)
            .create()
            .apply { show() }
    }
    private fun inserisciFilm(mediaType: String, title: String, regista: String, trama: String, durata: String) {
        // Ottieni l'UID dell'utente corrente
        val currentUserUID = FirebaseAuth.getInstance().currentUser?.uid

        if (currentUserUID != null) {
            // Ottieni un riferimento al database di Firebase
            val database = FirebaseDatabase.getInstance()

            // Ottieni un riferimento alla radice del database
            val rootRef = database.reference

            // Determina il percorso in base al tipo di media selezionato
            val mediaPath = if (mediaType == "Blu-ray") {
                "Bluray"
            } else {
                "DVD"
            }

            // Crea un nuovo nodo per il film con un ID univoco generato da push()
            val filmRef = rootRef.child(mediaPath).push()

            // Inserisci i dati del film direttamente nel database
            val filmData = mapOf(
                "title" to title,
                "regista" to regista,
                "trama" to trama,
                "durata" to durata,
                "uid" to currentUserUID
            )

            // Inserisci i dati nel database
            filmRef.setValue(filmData)
                .addOnSuccessListener {
                    // Inserimento riuscito
                    // Puoi gestire qui eventuali azioni da eseguire dopo l'inserimento
                    // Ad esempio, mostrare un messaggio di successo all'utente
                }
                .addOnFailureListener { exception ->
                    // Inserimento fallito
                    // Gestisci qui eventuali errori o eccezioni
                    println("Errore durante l'inserimento del film")
                }
        }
        else {
            // L'utente non è autenticato
            println("Errore in fase di login")
        }

    }
}