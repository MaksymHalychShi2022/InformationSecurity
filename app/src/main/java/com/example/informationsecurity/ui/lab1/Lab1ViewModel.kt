package com.example.informationsecurity.ui.lab1

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class Lab1ViewModel : ViewModel() {

    private val _output = MutableLiveData<String>().apply {
        value = "Here will be generated numbers!"
    }
    val output: LiveData<String> = _output

    fun updateOutput(output: String) {
        _output.value = output
    }
}