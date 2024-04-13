package com.uninsubria.myvideoteca.ui.cd

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CDViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "Inserisco cd"
    }
    val text: LiveData<String> = _text
}