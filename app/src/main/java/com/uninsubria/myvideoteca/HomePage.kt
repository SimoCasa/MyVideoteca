package com.uninsubria.myvideoteca

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.uninsubria.myvideoteca.databinding.ActivityMainBinding
import androidx.appcompat.widget.SearchView

@Suppress("DEPRECATION")
class HomePage : AppCompatActivity(){
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth
    private var backPressedOnce = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        //Prendo istanza
        auth = FirebaseAuth.getInstance()

        setContentView(binding.root)
        setSupportActionBar(binding.appBarMain.toolbar)

        //Verifico quale sia l'utente corrente
        val firebaseUser = auth.currentUser!!
        val ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.child(firebaseUser.uid)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                //Sopprimo per aggregare Nome e Cognome nella TextView.
                @SuppressLint("SetTextI18n")
                override fun onDataChange(snapshot: DataSnapshot) {
                    val name = snapshot.child("name").value
                    val surname = snapshot.child("surname").value
                    val email = snapshot.child("email").value
                    val rule = snapshot.child("rule").value
                    val imgUrl = snapshot.child("imgurl").value 
                    val imageView: ImageView = findViewById(R.id.imageView)
                    //Imposto icona profilo
                    Glide.with(this@HomePage)
                        .load(imgUrl)
                        .error(R.drawable.icon_user)    //Se errata mostra quella base
                        .into(imageView)

                    if (rule == "admin"){
                        // Modifica del valore della TextView (Nome)
                        binding.navView.getHeaderView(0).findViewById<TextView>(R.id.userDescTextView).apply {
                            text = name.toString() //MODIFICA
                        }
                        binding.navView.getHeaderView(0).findViewById<TextView>(R.id.textViewAdmin).setText("\uD83C\uDF1FADMIN\uD83C\uDF1F")
                    }else if (rule == "user"){
                        binding.appBarMain.fab.visibility= View.GONE
                        // Modifica del valore della TextView (Nome + Cognome)
                        binding.navView.getHeaderView(0).findViewById<TextView>(R.id.userDescTextView).apply {
                            text = "$name $surname" //Imposta nome e cognome nella sidebar
                        }
                    }
                    // Modifica del valore della TextView (Email)
                    binding.navView.getHeaderView(0).findViewById<TextView>(R.id.emailTextView).apply {
                        text = email.toString() // Imposta nome e cognome nella sidebar
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    //errore accesso DB Firebase
                }
            })

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_blueray, R.id.nav_dvd, R.id.nav_cd
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        //Rimuove e rimette la 'matita' in base a dove ci troviamo
        navController.addOnDestinationChangedListener { _, destination, _ ->
            ref.child(firebaseUser.uid)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    //Sopprimo per aggregare Nome e Cognome nella TextView.
                    @SuppressLint("SetTextI18n")
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val rule = snapshot.child("rule").value
                        if (rule == "admin"){
                            if (destination.id == R.id.adminOptionsFragment ||
                                destination.id == R.id.insertBRDVDFragment ||
                                destination.id == R.id.insertCDFragment ||
                                destination.id == R.id.editFragment ||
                                destination.id == R.id.insertCDFragment ||
                                destination.id == R.id.removeFragment) {
                                binding.appBarMain.fab.visibility = View.GONE
                            } else {
                                binding.appBarMain.fab.visibility = View.VISIBLE
                            }
                        }else if (rule == "user"){
                            if (destination.id == R.id.nav_cd ||
                                destination.id == R.id.nav_blueray ||
                                destination.id == R.id.nav_dvd) {
                                binding.appBarMain.fab.visibility = View.GONE
                            } else {
                                binding.appBarMain.fab.visibility = View.VISIBLE
                            }
                        }
                    }
                    override fun onCancelled(error: DatabaseError) {
                        //errore accesso DB Firebase
                    }
                })
        }
        //Bottone icona matita premuto
        val onClickListener = binding.appBarMain.fab.setOnClickListener {
            navController.navigate(R.id.adminOptionsFragment)
            //binding.appBarMain.fab.visibility= View.GONE
        }
    }




    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    @SuppressLint("MissingSuperCall")
    //DA SISTEMARE SE PREMO DUE VOLTE ESCO ANCHE NEI FRAGMENT
    override fun onBackPressed() {
        // Se non ci sono fragment nello stack, esegui la logica personalizzata per il pulsante indietro
        if (backPressedOnce) {
            // Se l'utente ha già premuto il pulsante indietro una volta, esegui l'operazione di default (tornare indietro)
            super.onBackPressed()
        } else {
            // Se è la prima volta che l'utente preme il pulsante indietro, mostra un messaggio di conferma
            Toast.makeText(this, "Premi nuovamente per tornare indietro", Toast.LENGTH_SHORT).show()
            // Imposta il flag a true
            backPressedOnce = true
            // Avvia un timer per reimpostare il flag dopo un certo periodo di tempo (ad esempio, 2 secondi)
            // In questo modo, se l'utente preme nuovamente il pulsante indietro entro questo tempo, verrà eseguita l'operazione di default
            // Se l'utente non preme nuovamente il pulsante indietro entro questo tempo, il flag viene reimpostato e il comportamento di default viene bloccato
            // Puoi personalizzare la durata del timer in base alle tue esigenze
            window.decorView.postDelayed({ backPressedOnce = false }, 2000)
        }
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        // Trova l'elemento di ricerca nel menu
        val searchItem = menu.findItem(R.id.app_bar_search)

        // Ottieni il SearchView dall'actionView dell'elemento di ricerca
        val searchView = searchItem.actionView as SearchView

        searchView.setOnSearchClickListener {
            // Quando la barra di ricerca viene aperta, rendi invisibile l'elemento delle impostazioni
            val settingsMenuItem = menu.findItem(R.id.action_settings)
            settingsMenuItem.isVisible = false
        }

        searchView.setOnCloseListener {
            // Quando la barra di ricerca viene chiusa, rendi visibile l'elemento delle impostazioni
            val settingsMenuItem = menu.findItem(R.id.action_settings)
            settingsMenuItem.isVisible = true
            false
        }

        return true
    }
}