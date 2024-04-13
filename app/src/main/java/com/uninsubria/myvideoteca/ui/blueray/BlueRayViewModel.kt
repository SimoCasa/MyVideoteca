package com.uninsubria.myvideoteca.ui.blueray

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class BlueRayViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "Inserisco blueray"
    }
    val text: LiveData<String> = _text
}