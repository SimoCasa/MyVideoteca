package com.uninsubria.myvideoteca.ui.blueray

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
import com.uninsubria.myvideoteca.DetailedBRDVD
import com.uninsubria.myvideoteca.R
import com.uninsubria.myvideoteca.databinding.FragmentBluerayBinding

class BlueRayFragment : Fragment() {

    private var _binding: FragmentBluerayBinding? = null
    private lateinit var listView: ListView
    private lateinit var adapter: BlueRayAdapter

    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBluerayBinding.inflate(inflater, container, false)
        val root: View = binding.root
        listView = binding.brListView
        // Inizializza l'adapter
        adapter = BlueRayAdapter(requireContext(), R.layout.fragment_blueray, mutableListOf())
        // Imposta l'adapter sulla ListView
        listView.adapter = adapter

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Barra di caricamento
        val progressBar = binding.progressBar

        // Recupera i dati da Firebase e aggiorna l'adapter
        retrieveDataFromFirebase(progressBar)

        listView.setOnItemClickListener { parent, view, position, id ->
            // Ottieni l'elemento selezionato dalla posizione
            val selectedBlueRay = adapter.getItem(position)

            // Passa i dati relativi all'elemento selezionato al layout dettagliato
            val intent = Intent(requireContext(), DetailedBRDVD::class.java)
            intent.putExtra("selectedBlueRay", selectedBlueRay)
            intent.putExtra("callingActivity", "ActivityBlue")
            startActivity(intent)
        }
    }

    private fun retrieveDataFromFirebase(progressBar: ProgressBar) {
        // Esempio di recupero dati da Firebase e aggiornamento dell'adapter
        val databaseReference = FirebaseDatabase.getInstance().getReference("Bluray")
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val blueRayList = mutableListOf<BlueRayItem>()

                for (snapshot in dataSnapshot.children) {
                    val blueRay = snapshot.getValue(BlueRayItem::class.java)
                    blueRay?.let { blueRayList.add(it) }
                }

                // Aggiorna l'adapter con i dati appena ottenuti
                adapter.updateData(blueRayList)

                // Nascondi la ProgressBar quando i dati sono stati caricati
                progressBar.visibility = View.GONE
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("BlueRayFragment", "Failed to retrieve data from Firebase: ${databaseError.message}")
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