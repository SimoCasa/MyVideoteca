package com.uninsubria.myvideoteca.ui.dvd

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.appcompat.widget.SearchView
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Indica che questo fragment ha un menu di opzioni che vuole popolare
        setHasOptionsMenu(true)
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDvdBinding.inflate(inflater, container, false)
        val root: View = binding.root
        listView = binding.dvdListView
        // Inizializza l'adapter
        adapter = DVDAdapter(requireContext(), R.layout.fragment_dvd, mutableListOf())
        // Imposta l'adapter sulla ListView
        listView.adapter = adapter

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Barra di caricamento
        val progressBar = binding.progressBar
        // Recupera i dati da Firebase e aggiorna l'adapter
        retrieveDataFromFirebase(null)

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

    private fun retrieveDataFromFirebase(searchQuery: String? = null) {
        // Esempio di recupero dati da Firebase e aggiornamento dell'adapter
        val databaseReference = FirebaseDatabase.getInstance().getReference("DVD")
        val query = if (searchQuery.isNullOrEmpty()) {
            databaseReference  // Nessuna ricerca specifica, carica tutti i dati
        } else {
            val formattedQuery = modWords(searchQuery.trim())
            databaseReference.orderByChild("title").startAt(formattedQuery).endAt(formattedQuery + "\uf8ff")
        }
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            @SuppressLint("RestrictedApi")
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val dvdList = mutableListOf<DVDItem>()
                for (snapshot in dataSnapshot.children) {
                    val dvd = snapshot.getValue(DVDItem::class.java)
                    dvd?.let {
                        it.ref = snapshot.ref.path.toString()  // Aggiungi il percorso del nodo corrente
                        dvdList.add(it) }
                }
                adapter.updateData(dvdList)
                binding.progressBar.visibility = View.GONE
            }
            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("BlueRayFragment", "Failed to retrieve data from Firebase: ${databaseError.message}")
                // Nascondi la ProgressBar anche in caso di errore
                binding.progressBar.visibility = View.GONE
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main, menu)
        super.onCreateOptionsMenu(menu, inflater)

        // Trova l'oggetto SearchView e imposta il listener
        val searchItem = menu.findItem(R.id.app_bar_search)
        val searchView = searchItem.actionView as androidx.appcompat.widget.SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (!query.isNullOrBlank()) {
                    //Log.d("SearchViewInput", "Query submitted: $query")
                    retrieveDataFromFirebase(query.trim())
                }
                return true
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                //Log.d("SearchViewInput", "Query text changed: $newText")
                if (newText.isNullOrBlank()) {
                    //Log.d("SearchViewInput", "Query text changed: Showing all items")
                    retrieveDataFromFirebase()  // Carica tutti gli elementi se non viene digitato nulla o il testo è cancellato
                }
                return true
            }
        })
        searchView.setOnSearchClickListener {
            // Quando la barra di ricerca viene aperta, rendi invisibile l'elemento delle impostazioni
            val settingsMenuItem = menu.findItem(R.id.action_logout)
            settingsMenuItem.isVisible = false
        }
        searchView.setOnCloseListener {
            // Quando la barra di ricerca viene chiusa, rendi visibile l'elemento delle impostazioni
            val settingsMenuItem = menu.findItem(R.id.action_logout)
            settingsMenuItem.isVisible = true
            false
        }
    }

    //Metodo per rendere prima minuscolo l'inserimento fatto dall'utente, e poi la prima lettera forzata maiuscola per ogni parola (come da DB)
    fun modWords(original: String?): String {
        if (original.isNullOrEmpty()) return ""
        return original.lowercase().split(" ").joinToString(" ") {
            it.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
        }
    }
}