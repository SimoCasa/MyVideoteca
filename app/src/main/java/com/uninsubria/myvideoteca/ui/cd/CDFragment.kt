package com.uninsubria.myvideoteca.ui.cd

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.uninsubria.myvideoteca.DetailedCD
import com.uninsubria.myvideoteca.R
import com.uninsubria.myvideoteca.databinding.FragmentCdBinding
import com.uninsubria.myvideoteca.ui.dvd.CDItem

class CDFragment : Fragment() {

    private var _binding: FragmentCdBinding? = null
    private lateinit var listView: ListView
    private lateinit var adapter: CDAdapter

    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCdBinding.inflate(inflater, container, false)
        val root: View = binding.root
        listView = binding.cdListView
        // Inizializza l'adapter
        adapter = CDAdapter(requireContext(), R.layout.fragment_cd, mutableListOf())
        // Imposta l'adapter sulla ListView
        listView.adapter = adapter

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val progressBar = binding.progressBar
        // Recupera i dati da Firebase e aggiorna l'adapter
        retrieveDataFromFirebase(progressBar)

        listView.setOnItemClickListener { parent, view, position, id ->
            // Ottieni l'elemento selezionato dalla posizione
            val selectedCD = adapter.getItem(position)

            // Passa i dati relativi all'elemento selezionato al layout dettagliato
            val intent = Intent(requireContext(), DetailedCD::class.java)
            intent.putExtra("selectedCD", selectedCD)
            startActivity(intent)
        }
    }

    private fun retrieveDataFromFirebase(progressBar: ProgressBar) {
        // Esempio di recupero dati da Firebase e aggiornamento dell'adapter
        val databaseReference = FirebaseDatabase.getInstance().getReference("CD")
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val cdList = mutableListOf<CDItem>()

                for (snapshot in dataSnapshot.children) {
                    val cd = snapshot.getValue(CDItem::class.java)
                    cd?.let { cdList.add(it) }
                }

                // Aggiorna l'adapter con i dati appena ottenuti
                adapter.updateData(cdList)

                // Nascondi la ProgressBar quando i dati sono stati caricati
                progressBar.visibility = View.GONE
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("CDFragment", "Failed to retrieve data from Firebase: ${databaseError.message}")
                // Nascondi la ProgressBar anche in caso di errore
                progressBar.visibility = View.GONE
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}