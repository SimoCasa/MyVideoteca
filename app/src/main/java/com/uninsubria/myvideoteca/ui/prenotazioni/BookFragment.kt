package com.uninsubria.myvideoteca.ui.prenotazioni

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.ProgressBar
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.uninsubria.myvideoteca.R
import com.uninsubria.myvideoteca.ui.blueray.BlueRayItem
import com.uninsubria.myvideoteca.ui.dvd.CDItem
import com.uninsubria.myvideoteca.ui.dvd.DVDItem

class BookFragment : Fragment() {

    private lateinit var listView: ListView
    private lateinit var adapter: BookAdapter
    private val mediaItems = mutableListOf<BookItem>()
    private lateinit var progressBar: ProgressBar
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_book, container, false)
        listView = view.findViewById(R.id.book_listView)
        adapter = BookAdapter(requireContext(), R.layout.list_item, mediaItems)
        listView.adapter = adapter
        progressBar = view.findViewById(R.id.progressBar) // Inizializza la ProgressBar
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fetchData()
        // Indica che questo fragment ha un menu di opzioni che vuole popolare
        setHasOptionsMenu(true)
    }

    private fun fetchData() {
        val database = FirebaseDatabase.getInstance()

        val mediaReference = database.reference

        mediaReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Ottieni l'UID dell'utente corrente
                val currentUserUid = FirebaseAuth.getInstance().currentUser?.uid
                for (mediaSnapshot in dataSnapshot.children) {
                    when (mediaSnapshot.key) {
                        "Bluray" -> {
                            for (snapshot in mediaSnapshot.children) {
                                val item = snapshot.getValue(BlueRayItem::class.java)
                                item?.let {
                                    // Verifica se l'UID dell'utente corrente corrisponde a quello dell'elemento
                                    if (currentUserUid == it.userId) {
                                        mediaItems.add(
                                            BookItem(
                                                it.title,
                                                it.locandina,
                                                it.ref,
                                                it.available,
                                                it.userId,
                                                "Blu-ray"
                                            )
                                        )
                                    }
                                }
                            }
                        }
                        "CD" -> {
                            for (snapshot in mediaSnapshot.children) {
                                val item = snapshot.getValue(CDItem::class.java)
                                item?.let {
                                    // Verifica se l'UID dell'utente corrente corrisponde a quello dell'elemento
                                    if (currentUserUid == it.userId) {
                                        mediaItems.add(
                                            BookItem(
                                                it.title,
                                                it.img,
                                                it.ref,
                                                it.available,
                                                it.userId,
                                                "CD"
                                            )
                                        )
                                    }
                                }
                            }
                        }
                        "DVD" -> {
                            for (snapshot in mediaSnapshot.children) {
                                val item = snapshot.getValue(DVDItem::class.java)
                                item?.let {
                                    // Verifica se l'UID dell'utente corrente corrisponde a quello dell'elemento
                                    if (currentUserUid == it.userId) {
                                        mediaItems.add(
                                            BookItem(
                                                it.title,
                                                it.locandina,
                                                it.ref,
                                                it.available,
                                                it.userId,
                                                "DVD"
                                            )
                                        )
                                    }
                                }
                            }
                        }
                        else -> {
                            // Handle unknown media type
                        }
                    }
                }
                adapter.notifyDataSetChanged()
                // Nascondi la ProgressBar dopo il caricamento dei dati
                progressBar.visibility = View.GONE
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle errors
            }
        })
    }

    // AGGIUNGERE METODO PER CERCARE

    //..

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
                    //retrieveDataFromFirebase(query.trim())
                }
                return true
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                //Log.d("SearchViewInput", "Query text changed: $newText")
                if (newText.isNullOrBlank()) {
                    //Log.d("SearchViewInput", "Query text changed: Showing all items")
                    ///retrieveDataFromFirebase()  // Carica tutti gli elementi se non viene digitato nulla o il testo è cancellato
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