package com.uninsubria.myvideoteca.ui.dvd

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class DVDViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "Inserisco dvd"
    }
    val text: LiveData<String> = _text
}