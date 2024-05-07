package com.uninsubria.myvideoteca.ui.dvd

import java.io.Serializable

data class DVDItem( val durata: String="",
                    val regista: String="",
                    val title: String="",
                    val trama: String="",
                    var ref: String="",
                    var available: Boolean = true,
                    var userId: String ="",
                    val locandina: String="") : Serializable{

    // Costruttore senza argomenti richiesto da Firebase
    constructor() : this("", "", "", "", "",true)
}