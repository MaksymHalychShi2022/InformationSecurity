package com.example.informationsecurity.ui.lab3

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class Lab3ViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is Lab 3 Fragment"
    }
    val text: LiveData<String> = _text
}