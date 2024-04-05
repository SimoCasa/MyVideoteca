package com.uninsubria.myvideoteca.ui.slideshow

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SlideshowViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "Questo è un Fragment slideshow"
    }
    val text: LiveData<String> = _text
}