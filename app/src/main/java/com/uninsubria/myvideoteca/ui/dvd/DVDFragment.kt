package com.uninsubria.myvideoteca.ui.dvd

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.fragment.app.Fragment
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.uninsubria.myvideoteca.DetailedBRDVD
import com.uninsubria.myvideoteca.R
import com.uninsubria.myvideoteca.databinding.FragmentDvdBinding

class DVDFragment : Fragment() {

    private var _binding: FragmentDvdBinding? = null
    private lateinit var listView: ListView
    private lateinit var adapter: DVDAdapter

    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDvdBinding.inflate(inflater, container, false)
        val root: View = binding.root
        listView = binding.brListView
        // Inizializza l'adapter
        adapter = DVDAdapter(requireContext(), R.layout.fragment_dvd, mutableListOf())
        // Imposta l'adapter sulla ListView
        listView.adapter = adapter

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Recupera i dati da Firebase e aggiorna l'adapter
        retrieveDataFromFirebase()

        listView.setOnItemClickListener { parent, view, position, id ->
            // Ottieni l'elemento selezionato dalla posizione
            val selectedDVD = adapter.getItem(position)

            // Passa i dati relativi all'elemento selezionato al layout dettagliato
            val intent = Intent(requireContext(), DetailedBRDVD::class.java)
            intent.putExtra("selectedDVD", selectedDVD)
            intent.putExtra("callingActivity", "ActivityDVD")
            startActivity(intent)
        }
    }

    private fun retrieveDataFromFirebase() {
        // Esempio di recupero dati da Firebase e aggiornamento dell'adapter
        val databaseReference = FirebaseDatabase.getInstance().getReference("DVD")
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val dvdList = mutableListOf<DVDItem>()

                for (snapshot in dataSnapshot.children) {
                    val dvd = snapshot.getValue(DVDItem::class.java)
                    dvd?.let { dvdList.add(it) }
                }

                // Aggiorna l'adapter con i dati appena ottenuti
                adapter.updateData(dvdList)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("DVDFragment", "Failed to retrieve data from Firebase: ${databaseError.message}")
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}