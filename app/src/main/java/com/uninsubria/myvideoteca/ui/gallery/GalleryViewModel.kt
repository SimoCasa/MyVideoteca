package com.uninsubria.myvideoteca.ui.gallery

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class GalleryViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "Questo è un Fragment galleria"
    }
    val text: LiveData<String> = _text
}