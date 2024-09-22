package com.example.informationsecurity.ui.lab1

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class Lab1ViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is Lab 1 Fragment"
    }
    val text: LiveData<String> = _text
}