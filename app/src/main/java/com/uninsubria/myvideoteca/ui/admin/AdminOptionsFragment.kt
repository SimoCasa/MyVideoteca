package com.uninsubria.myvideoteca.ui.admin

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.fragment.findNavController
import com.uninsubria.myvideoteca.R

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AdminOptionsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AdminOptionsFragment : Fragment() {
    //val binding = ActivityMainBinding.inflate(layoutInflater)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_admin_options, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Ottieni un riferimento al NavController
        val navController = findNavController()

        // Bottone aggiungi BRDVD
        val btnAddBRDVD = view.findViewById<Button>(R.id.btn_add_brdvd)
        btnAddBRDVD.setOnClickListener {
            navController.navigate(R.id.insertBRDVDFragment)
        }

        //Bottone aggiungi CD
        val btnAddCD = view.findViewById<Button>(R.id.btn_add_cd)
        btnAddCD.setOnClickListener {
            navController.navigate(R.id.insertCDFragment)
        }

        //Bottone Aggiungi
        val btnEditArticle = view.findViewById<Button>(R.id.btn_edit_article)
        btnEditArticle.setOnClickListener {
            navController.navigate(R.id.editFragment)
        }

        val btnRemoveArticle = view.findViewById<Button>(R.id.btn_remove_article)
        btnRemoveArticle.setOnClickListener {
            navController.navigate(R.id.removeFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        //binding.appBarMain.fab.visibility= View.VISIBLE
    }
}