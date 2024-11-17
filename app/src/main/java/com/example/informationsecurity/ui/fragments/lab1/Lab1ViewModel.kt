package com.example.informationsecurity.ui.fragments.lab1

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class Lab1ViewModel : ViewModel() {

    private val _generatedNumbers = MutableLiveData<String>()
    val generatedNumbers: LiveData<String> = _generatedNumbers

    private val _estimatedPi = MutableLiveData<Double>()
    val estimatedPi: LiveData<Double> = _estimatedPi

    fun updateGeneratedNumbers(newValue: String) {
        _generatedNumbers.value = newValue
    }

    fun updateEstimatedPi(newValue: Double) {
        _estimatedPi.value = newValue
    }
}