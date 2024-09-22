package com.example.informationsecurity.ui.lab2

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class Lab2ViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is Lab 2 Fragment"
    }
    val text: LiveData<String> = _text
}