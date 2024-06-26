package com.uninsubria.myvideoteca.ui.admin

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
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
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import androidx.navigation.fragment.findNavController
import com.google.firebase.database.FirebaseDatabase
import com.uninsubria.myvideoteca.HomePage
import com.uninsubria.myvideoteca.MainActivity

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
        val editTextLocandina = view.findViewById<EditText>(R.id.editTextLocandina)
        val btnAdd = view.findViewById<Button>(R.id.btnAdd)

        // Click listener per il bottone btnAdd
        btnAdd.setOnClickListener {
            // Recupera il tipo di media selezionato dall'utente
            val mediaType = when {
                radioButtonBlueRay.isChecked -> "Blu-ray"
                radioButtonDVD.isChecked -> "DVD"
                else -> ""
            }

            //Controllo che i dati inseriti siano validi
            validateFields(
                editTextTitle,
                editTextRegista,
                editTextTrama,
                editTextHours,
                editTextMinutes,
                editTextSeconds,
                editTextLocandina
            ) { isValid ->
                if (isValid) {  // Recupera i valori inseriti dall'utente
                    val title = modWords(editTextTitle.text.toString())
                    val regista = editTextRegista.text.toString()
                    val trama = editTextTrama.text.toString()
                    val hours = editTextHours.text.toString().toIntOrNull() ?: 0
                    val minutes = editTextMinutes.text.toString().toIntOrNull() ?: 0
                    val seconds = editTextSeconds.text.toString().toIntOrNull() ?: 0
                    val durata = "$hours:$minutes:$seconds"
                    val locandina = editTextLocandina.text.toString()
                    //Inserisci i film nel database
                    inserisciFilm(mediaType, title, regista, trama, durata,locandina)
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
    //Creo messaggio di dialogo
    private fun createLoadingDialog(message: String): AlertDialog {
        return MaterialAlertDialogBuilder(requireContext())
            .setMessage(message)
            .setCancelable(false)
            .create()
            .apply { show() }
    }
    //Inserisco Film nel DB di Firebase
    private fun inserisciFilm(mediaType: String, title: String, regista: String, trama: String, durata: String, locandina:String) {
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
                "uid" to currentUserUID,
                "available" to true, // validità impostata su 'True' di base.
                "locandina" to locandina
            )

            // Inserisci i dati nel database
            filmRef.setValue(filmData)
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
    registaField: EditText,
    tramaField: EditText,
    hoursField: EditText,
    minutesField: EditText,
    secondsField: EditText,
    locandinaField : EditText,
    callback: (Boolean) -> Unit
    ) {
        var isValid = true

        if (titleField.text.isBlank() || registaField.text.isBlank() || tramaField.text.isBlank() || hoursField.text.isBlank() || minutesField.text.isBlank() || secondsField.text.isBlank() || minutesField.text.toString().toInt()>59 || hoursField.text.toString().toInt()>59 || secondsField.text.toString().toInt()>59) {
            isValid = false
            // Imposta il backgroundTintList di rosso per i campi vuoti
            if (titleField.text.isBlank()){
                titleField.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#D8E14949"))
                titleField.error="Campo Obbligatorio"}
            else{
                titleField.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#D883E149"))
                titleField.error=null}

            if (registaField.text.isBlank()) {
                registaField.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#D8E14949"))
                registaField.error="Campo Obbligatorio"}
            else{
                registaField.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#D883E149"))
                registaField.error=null
            }

            if (tramaField.text.isBlank()){
                tramaField.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#D8E14949"))
                tramaField.error="Campo Obbligatorio"}
            else{
                tramaField.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#D883E149"))
                tramaField.error=null
            }
            //Controlli su ore minuti e secondi
            if (hoursField.text.isBlank() || hoursField.text.toString().toIntOrNull()==null){
                hoursField.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#D8E14949"))
                hoursField.error="Campo Obbligatorio"}
            else{
                hoursField.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#D883E149"))
                hoursField.error=null
            }
            if (minutesField.text.isBlank() || minutesField.text.toString().toInt()>59 || minutesField.text.toString().toIntOrNull()==null){
                minutesField.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#D8E14949"))
                minutesField.error="Campo Obbligatorio"}
            else{
                minutesField.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#D883E149"))
                minutesField.error=null
            }
            if (secondsField.text.isBlank() || secondsField.text.toString().toInt()>59 || secondsField.text.toString().toIntOrNull()==null){
                secondsField.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#D8E14949"))
                secondsField.error="Campo Obbligatorio"}
            else{
                secondsField.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#D883E149"))
                secondsField.error=null
            }
            //La locandina è facoltativa
        } else {
            // Imposta il backgroundTintList di VERDE per i campi compilati correttamente
            titleField.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#D883E149"))
            registaField.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#D883E149"))
            tramaField.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#D883E149"))
            hoursField.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#D883E149"))
            minutesField.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#D883E149"))
            secondsField.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#D883E149"))
        }

        callback(isValid)
    }

    //Metodo per rendere prima minuscolo l'inserimento fatto dall'utente nel box 'Title', e poi la prima lettera forzata maiuscola per ogni parola (utile per DB)
    fun modWords(original: String?): String {
        if (original.isNullOrEmpty()) return ""
        return original.lowercase().split(" ").joinToString(" ") {
            it.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
        }
    }
}