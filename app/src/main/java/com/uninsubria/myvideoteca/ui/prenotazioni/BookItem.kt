package com.uninsubria.myvideoteca.ui.prenotazioni

data class BookItem(
    val title: String = "", // Title del contenuto
    val imageUrl: String = "",
    val ref: String = "",
    var available: Boolean = true,
    var userId: String = "", // UID del utente
    val type: String = "" // "Blu-ray", "CD", "DVD"
)



