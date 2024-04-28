package com.uninsubria.myvideoteca.ui.dvd

import java.io.Serializable

data class CDItem(val durata: String="",
                   val autori: String="",
                   val title: String="",
                   val casa_discografica: String="",
                   val genere: String="",
                   val track: String="",
                  var ref: String="",
                   val img: String="") : Serializable {

    // Costruttore senza argomenti richiesto da Firebase
    constructor() : this("", "", "", "", "","", "")
}
