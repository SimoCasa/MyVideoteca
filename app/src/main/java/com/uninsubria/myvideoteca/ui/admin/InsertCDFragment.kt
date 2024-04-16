package com.uninsubria.myvideoteca.ui.admin

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
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
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_insert_cd, container, false)
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

        if (titleField.text.isBlank() || registaField.text.isBlank() || tramaField.text.isBlank() || hoursField.text.isBlank() || minutesField.text.isBlank() || secondsField.text.isBlank()) {
            isValid = false
            // Imposta il backgroundTintList di rosso per i campi vuoti
            if (titleField.text.isBlank()) titleField.backgroundTintList = ColorStateList.valueOf(
                Color.RED)
            if (registaField.text.isBlank()) registaField.backgroundTintList = ColorStateList.valueOf(Color.RED)
            if (tramaField.text.isBlank()) tramaField.backgroundTintList = ColorStateList.valueOf(Color.RED)
            //Controlli su ore minuti e secondi
            if (hoursField.text.isBlank() || hoursField.text.toString().toIntOrNull()==null) hoursField.backgroundTintList = ColorStateList.valueOf(Color.RED)
            if (minutesField.text.isBlank() || minutesField.text.toString().toInt()>59 || minutesField.text.toString().toIntOrNull()==null) minutesField.backgroundTintList = ColorStateList.valueOf(Color.RED)
            if (secondsField.text.isBlank() || secondsField.text.toString().toInt()>59 || secondsField.text.toString().toIntOrNull()==null) secondsField.backgroundTintList = ColorStateList.valueOf(Color.RED)
            //La locandiina è facoltativa
        } else {
            // Imposta il backgroundTintList di verde per i campi compilati correttamente
            titleField.backgroundTintList = ColorStateList.valueOf(Color.GREEN)
            registaField.backgroundTintList = ColorStateList.valueOf(Color.GREEN)
            tramaField.backgroundTintList = ColorStateList.valueOf(Color.GREEN)
            hoursField.backgroundTintList = ColorStateList.valueOf(Color.GREEN)
            minutesField.backgroundTintList = ColorStateList.valueOf(Color.GREEN)
            secondsField.backgroundTintList = ColorStateList.valueOf(Color.GREEN)
        }

        callback(isValid)
    }
}