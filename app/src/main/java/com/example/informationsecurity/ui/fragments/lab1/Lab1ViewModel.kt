package com.example.informationsecurity.ui.fragments.lab1

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.informationsecurity.utils.LehmerRandomNumberGenerator
import com.example.informationsecurity.utils.OperationState
import kotlinx.coroutines.launch

class Lab1ViewModel : ViewModel() {

    private val _generatedNumbers = MutableLiveData<String>()
    val generatedNumbers: LiveData<String> = _generatedNumbers

    private val _estimatedPi = MutableLiveData<Double>()
    val estimatedPi: LiveData<Double> = _estimatedPi

    private val _generator = LehmerRandomNumberGenerator()

    fun generateRandomNumbers(lengthOfSequence: Long): LiveData<OperationState<Unit>> {
        val operationState = MutableLiveData<OperationState<Unit>>()

        viewModelScope.launch {
            operationState.postValue(OperationState.Loading())

            try {
                // Generate the sequence
                val generatedNumbers = _generator.generateSequence(lengthOfSequence)
                val generatedNumbersString = generatedNumbers.joinToString("\n")

                // Update the generated numbers LiveData
                _generatedNumbers.postValue(generatedNumbersString)

                // Mark the operation as successful
                operationState.postValue(OperationState.Success(Unit))
            } catch (e: Exception) {
                // Post the error
                operationState.postValue(OperationState.Error("Error: ${e.message}"))
            }
        }

        return operationState
    }

    fun updateEstimatedPi(newValue: Double) {
        _estimatedPi.value = newValue
    }
}