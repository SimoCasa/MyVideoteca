package com.uninsubria.myvideoteca

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Menu
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.uninsubria.myvideoteca.databinding.ActivityMainBinding

class HomePage : AppCompatActivity(){
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        //Prendo istanza
        auth = FirebaseAuth.getInstance()

        setContentView(binding.root)
        setSupportActionBar(binding.appBarMain.toolbar)

        binding.appBarMain.fab.setOnClickListener {

            //Faccio qualche funzione
        }

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
                    if (rule == "admin"){
                        // Modifica del valore della TextView (Nome)
                        binding.navView.getHeaderView(0).findViewById<TextView>(R.id.userDescTextView).apply {
                            text = name.toString() //MODIFICA
                        }
                    }
                    else if (rule == "user"){
                        // Modifica del valore della TextView (Nome + Cognome)
                        binding.navView.getHeaderView(0).findViewById<TextView>(R.id.userDescTextView).apply {
                            text = "$name $surname" //MODIFICA
                        }
                    }
                    // Modifica del valore della TextView (Email)
                    binding.navView.getHeaderView(0).findViewById<TextView>(R.id.emailTextView).apply {
                        text = email.toString() // MODIFICA
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    //errore accesso DB Firebase
                }
            })
        //...

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

}