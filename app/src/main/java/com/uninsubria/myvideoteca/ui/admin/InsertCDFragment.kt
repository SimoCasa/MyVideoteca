package com.uninsubria.myvideoteca.ui.admin

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.uninsubria.myvideoteca.HomePage
import com.uninsubria.myvideoteca.R

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [InsertCDFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class InsertCDFragment : Fragment() {
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
        val view = inflater.inflate(R.layout.fragment_insert_cd, container, false)
        //Recupero sessione Firebase
        auth = FirebaseAuth.getInstance()

        // Recupera riferimenti agli elementi UI
        val editTextNomeDisco = view.findViewById<EditText>(R.id.editTextNomeDisco)
        val editTextAutori = view.findViewById<EditText>(R.id.editTextAutori)
        val editTextHours = view.findViewById<EditText>(R.id.editTextHours)
        val editTextMinutes = view.findViewById<EditText>(R.id.editTextMinutes)
        val editTextSeconds = view.findViewById<EditText>(R.id.editTextSeconds)
        val editTextCasaDisco = view.findViewById<EditText>(R.id.editTextCasaDisco)
        val editTextGenere = view.findViewById<EditText>(R.id.editTextGenere)
        val editTextTrack = view.findViewById<EditText>(R.id.editTextTrack)
        val editTextImgDisk = view.findViewById<EditText>(R.id.editTextImgDisk)
        val btnAdd = view.findViewById<Button>(R.id.btnAdd)

        // Click listener per il bottone btnAdd
        btnAdd.setOnClickListener {
            //Controllo che i dati inseriti siano validi
            validateFields(
                editTextNomeDisco,
                editTextAutori,
                editTextHours,
                editTextMinutes,
                editTextSeconds,
                editTextCasaDisco,
                editTextGenere,
                editTextTrack,
                editTextImgDisk
            ) { isValid ->
                if (isValid) {  // Recupera i valori inseriti dall'utente
                    val title = editTextNomeDisco.text.toString()
                    val autori = editTextAutori.text.toString()
                    val hours = editTextHours.text.toString().toIntOrNull() ?: 0
                    val minutes = editTextMinutes.text.toString().toIntOrNull() ?: 0
                    val seconds = editTextSeconds.text.toString().toIntOrNull() ?: 0
                    val casa_discografica = editTextCasaDisco.text.toString()
                    val genere = editTextGenere.text.toString()
                    val track = editTextTrack.text.toString()
                    val durata = "$hours:$minutes:$seconds"
                    val img = editTextImgDisk.text.toString()
                    //Inserisci i film nel database
                    inserisciCD(title, autori, casa_discografica, genere, track, durata,img)
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Si prega di compilare tutti i campi",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            // Mostro il progress durante l'inserimento
            loadingDialog = createLoadingDialog("Inserisco i dati...")

            // Termino il progress durante l'inserimento
            Handler().postDelayed({
                loadingDialog.dismiss()
            }, 100) // Ritardo di 0.1 secondo prima di chiudere il dialogo

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
         * @return A new instance of fragment InsertCD.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            InsertCDFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
    //Creo messaggio di dialogo
    private fun createLoadingDialog(message: String): AlertDialog {
        return MaterialAlertDialogBuilder(requireContext())
            .setMessage(message)
            .setCancelable(false)
            .create()
            .apply { show() }
    }
    //Inserisco Film nel DB di Firebase
    private fun inserisciCD(title: String, autori: String, casa_discografica: String, genere: String, track: String, durata: String, img:String) {

        // Ottieni l'UID dell'utente corrente
        val currentUserUID = FirebaseAuth.getInstance().currentUser?.uid

        if (currentUserUID != null) {
            // Ottieni un riferimento al database di Firebase
            val database = FirebaseDatabase.getInstance()

            // Ottieni un riferimento alla radice del database
            val rootRef = database.reference

            // Determina il percorso in base al tipo di media selezionato
            val mediaPath = "CD"

            // Crea un nuovo nodo per il film con un ID univoco generato da push()
            val filmRef = rootRef.child(mediaPath).push()

            // Inserisci i dati del film direttamente nel database
            val cdData = mapOf(
                "title" to title,
                "autori" to autori,
                "casa discografica" to casa_discografica,
                "genere" to genere,
                "track" to track,
                "durata" to durata,
                "img" to img
            )

            // Inserisci i dati nel database
            filmRef.setValue(cdData)
                .addOnSuccessListener {
                    // Inserimento riuscito
                    Toast.makeText(requireContext(), "Articolo inserito con successo", Toast.LENGTH_SHORT).show()
                    val intentHome = Intent(requireActivity(), HomePage::class.java)
                    startActivity(intentHome)
                }
                .addOnFailureListener { exception ->
                    // Inserimento fallito
                    Toast.makeText(requireContext(), "Errore nell'inserimento", Toast.LENGTH_SHORT).show()
                }
        }
        else {
            // L'utente non è autenticato
            println("Errore in fase di login")
        }
    }

    //Funzione per validare tutti i dati
    private fun validateFields(
        titleField: EditText,
        autoriField: EditText,
        hoursField: EditText,
        minutesField: EditText,
        secondsField: EditText,
        casadiscField: EditText,
        genereField: EditText,
        trackNField: EditText,
        imgField : EditText,

        callback: (Boolean) -> Unit
    ) {
        var isValid = true

        if (titleField.text.isBlank() || autoriField.text.isBlank() || hoursField.text.isBlank() || minutesField.text.isBlank() || secondsField.text.isBlank() || casadiscField.text.isBlank() || genereField.text.isBlank() || trackNField.text.isBlank() || minutesField.text.toString().toInt()>59 || hoursField.text.toString().toInt()>59 || secondsField.text.toString().toInt()>59) {
            isValid = false
            // Imposta il backgroundTintList di rosso per i campi vuoti
            if (titleField.text.isBlank()){
                titleField.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#D8E14949"))
                titleField.error="Campo obbligatorio"
            }else{
                titleField.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#D883E149"))
                titleField.error=null
            }

            if (autoriField.text.isBlank()) {
                autoriField.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#D8E14949"))
                titleField.error="Campo obbligatorio"
            }else{
                autoriField.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#D883E149"))
                titleField.error=null
            }

            if (casadiscField.text.isBlank()) {
                casadiscField.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#D8E14949"))
                titleField.error="Campo obbligatorio"
            }else{
                casadiscField.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#D883E149"))
                titleField.error=null
            }
            if (genereField.text.isBlank()){
                genereField.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#D8E14949"))
                titleField.error="Campo obbligatorio"
            }else{
                genereField.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#D883E149"))
                titleField.error=null
            }

            //Controlli su ore minuti e secondi
            if (hoursField.text.isBlank() || hoursField.text.toString().toIntOrNull()==null) {
                hoursField.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#D8E14949"))
                titleField.error="Campo obbligatorio"
            }else{
                hoursField.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#D883E149"))
                titleField.error=null
            }
            if (minutesField.text.isBlank() || minutesField.text.toString().toInt()>59 || minutesField.text.toString().toIntOrNull()==null) {
                minutesField.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#D8E14949"))
                titleField.error="Campo obbligatorio"
            }else{
                minutesField.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#D883E149"))
                titleField.error=null
            }
            if (secondsField.text.isBlank() || secondsField.text.toString().toInt()>59 || secondsField.text.toString().toIntOrNull()==null) {
                secondsField.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#D8E14949"))
                titleField.error="Campo obbligatorio"
            }else{
                secondsField.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#D883E149"))
                titleField.error=null
            }
            //Controlli sul numero tracce
            if (trackNField.text.isBlank() || trackNField.text.toString().toIntOrNull()==null) {
                trackNField.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#D8E14949"))
                titleField.error="Campo obbligatorio"
            }else{
                trackNField.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#D883E149"))
                titleField.error=null
            }
            //La immagine è facoltativa
        } else {
            // Imposta il backgroundTintList di verde per i campi compilati correttamente
            titleField.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#D883E149"))
            autoriField.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#D883E149"))
            hoursField.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#D883E149"))
            minutesField.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#D883E149"))
            secondsField.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#D883E149"))
            casadiscField.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#D883E149"))
            genereField.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#D883E149"))
            trackNField.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#D883E149"))
        }

        callback(isValid)
    }
}