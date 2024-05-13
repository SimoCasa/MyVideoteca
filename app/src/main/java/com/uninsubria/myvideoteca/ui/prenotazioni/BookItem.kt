package com.uninsubria.myvideoteca.ui.prenotazioni

data class BookItem(
    val title: String = "", // Title del contenuto
    val imageUrl: String = "", // immagine del contenuto
    var ref: String = "",
    var available: Boolean = true,
    var userId: String = "", // UID del utente
    var type: String = "" // "Blu-ray", "CD", "DVD"
)



