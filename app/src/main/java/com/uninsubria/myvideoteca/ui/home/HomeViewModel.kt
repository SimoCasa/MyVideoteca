package com.uninsubria.myvideoteca.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class HomeViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "Questo è un Fragment home"
    }
    val text: LiveData<String> = _text
}