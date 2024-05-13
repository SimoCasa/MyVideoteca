package com.uninsubria.myvideoteca.ui.prenotazioni

import android.content.ContentValues.TAG
import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
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
    private lateinit var txtNoArticle: TextView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_book, container, false)
        listView = view.findViewById(R.id.book_listView)
        adapter = BookAdapter(requireContext(), R.layout.list_item, mediaItems)
        listView.adapter = adapter
        progressBar = view.findViewById(R.id.progressBar) // Inizializza la ProgressBar
        txtNoArticle = view.findViewById(R.id.noArticle)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fetchData()
        //Quando viene premuto un elemento
        listView.setOnItemClickListener { parent, view, position, id ->
            val selectedItem = mediaItems[position]
            showConfirmationDialog(selectedItem)
        }
        // Indica che questo fragment ha un menu di opzioni che vuole popolare
        setHasOptionsMenu(true)
    }


    private fun fetchData(searchQuery: String? = null) {
        val database = FirebaseDatabase.getInstance()
        val mediaReference = database.reference
        val currentUserUid = FirebaseAuth.getInstance().currentUser?.uid

        mediaReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                mediaItems.clear()  // Clear before adding new items
                for (mediaSnapshot in dataSnapshot.children) {
                    when (mediaSnapshot.key) {
                        "Bluray" -> {
                            for (snapshot in mediaSnapshot.children) {
                                val item = snapshot.getValue(BlueRayItem::class.java)
                                item?.let {
                                    if (currentUserUid == it.userId && (searchQuery.isNullOrEmpty() || it.title.contains(searchQuery, ignoreCase = true))) {
                                        it.ref = snapshot.key ?: ""
                                        mediaItems.add(
                                            BookItem(
                                                it.title,
                                                it.locandina,  // Assuming 'locandina' is the field for the image in Bluray items
                                                it.ref,
                                                it.available,
                                                it.userId,
                                                "Bluray"
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
                                    if (currentUserUid == it.userId && (searchQuery.isNullOrEmpty() || it.title.contains(searchQuery, ignoreCase = true))) {
                                        it.ref = snapshot.key ?: ""
                                        mediaItems.add(
                                            BookItem(
                                                it.title,
                                                it.img,  // Assuming 'img' is the field for the image in CD items
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
                                    if (currentUserUid == it.userId && (searchQuery.isNullOrEmpty() || it.title.contains(searchQuery, ignoreCase = true))) {
                                        it.ref = snapshot.key ?: ""
                                        mediaItems.add(
                                            BookItem(
                                                it.title,
                                                it.locandina,  // Assuming DVDs use the same field as Bluray for images
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
                txtNoArticle.visibility = if (mediaItems.isEmpty()) View.VISIBLE else View.GONE
                adapter.notifyDataSetChanged()
                progressBar.visibility = View.GONE
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e(TAG, "Failed to load data: ${databaseError.message}")
                progressBar.visibility = View.GONE
            }
        })
    }
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main, menu)
        super.onCreateOptionsMenu(menu, inflater)

        val searchItem = menu.findItem(R.id.app_bar_search)
        val searchView = searchItem.actionView as androidx.appcompat.widget.SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { fetchData(modWords(it)) }
                return true
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText.isNullOrBlank()) {
                    fetchData() // Fetch all items if text is cleared
                } else {
                    fetchData(modWords(newText))
                }
                return true
            }
        })
        searchView.setOnSearchClickListener {
            val settingsMenuItem = menu.findItem(R.id.action_logout)
            settingsMenuItem.isVisible = false
        }
        searchView.setOnCloseListener {
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

    //Funzione Per mostrare l'icona conferma restituzione
    private fun showConfirmationDialog(selectedItem: BookItem) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Conferma restituzione")
        builder.setMessage("Sei sicuro di voler restituire ${selectedItem.title}?")
        // Personalizza il testo del pulsante "Elimina" per renderlo rosso
        val positiveText = SpannableString("Restituisci")
        positiveText.setSpan(ForegroundColorSpan(Color.RED), 0, positiveText.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        builder.setPositiveButton(positiveText) { dialog, which ->
            // Aggiorna lo stato di prenotazione
            updateBookingStatus(selectedItem)
        }
        builder.setNegativeButton("Annulla", null)
        builder.show()
    }

    private fun updateBookingStatus(selectedItem: BookItem) {
        val database = FirebaseDatabase.getInstance()
        // Verifica che il riferimento sia valido e non vuoto
        if (selectedItem.ref.isNotEmpty()) {
            val myRef = database.getReference(selectedItem.type).child(selectedItem.ref)
            Log.d("REF", selectedItem.ref)
            // Crea una mappa per aggiornare i valori desiderati nel database
            val updates = hashMapOf<String, Any>(
                "userId" to "null",
                "available" to true
            )
            myRef.updateChildren(updates)
                .addOnSuccessListener {
                    // Success: Aggiorna lo stato locale dell'oggetto
                    selectedItem.userId = "null"
                    selectedItem.available = true
                    // Notifica l'adapter delle modifiche
                    adapter.notifyDataSetChanged()
                    // Ricarica la ListView
                    reloadListView()
                    Toast.makeText(context, "${selectedItem.title} restituito con successo", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { exception ->
                    // Handle failure
                    Log.e(TAG, "Errore durante l'aggiornamento dell'elemento: ", exception)
                    Toast.makeText(context, "${selectedItem.title} non restituito", Toast.LENGTH_SHORT).show()
                }
        } else {
            // Handle case where the reference is not properly set
            Log.e(TAG, "Riferimento non valido per l'elemento selezionato: ${selectedItem.title}")
            Toast.makeText(context, "Errore: Rif dell'elemento non trovato.", Toast.LENGTH_LONG).show()
        }
    }
    private fun reloadListView() {
        mediaItems.clear() // Pulisce la lista dei mediaItems
        fetchData() // Ricarica i dati dalla Firebase
    }

}