package com.example.informationsecurity.ui.fragments.lab1

import android.app.Application
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.informationsecurity.ui.MainViewModel
import com.example.informationsecurity.utils.LehmerRandomNumberGenerator
import com.example.informationsecurity.utils.RandomNumbersUtils

class Lab1ViewModel(application: Application) : MainViewModel(application) {

    private val _generatedNumbers = MutableLiveData<String>()
    val generatedNumbers: LiveData<String> = _generatedNumbers

    private val _estimatedPi = MutableLiveData<Double>()
    val estimatedPi: LiveData<Double> = _estimatedPi

    private val _generator = LehmerRandomNumberGenerator()

    suspend fun saveGeneratedNumbers(uri: Uri) {
        generatedNumbers.value?.let {
            writeToFile(uri, it)
        }
    }

    suspend fun loadGeneratedNumbers(uri: Uri) {
        val content = readFromFile(uri)
        _generatedNumbers.postValue(content)
    }

    suspend fun generateRandomNumbers(lengthOfSequence: Long) {
        // Generate the sequence
        val generatedNumbers = _generator.generateSequence(lengthOfSequence)
        val generatedNumbersString = generatedNumbers.joinToString("\n")

        // Update the generated numbers LiveData
        _generatedNumbers.postValue(generatedNumbersString)
    }


    suspend fun estimatePi(numberOfPairs: Long, useStandardLib: Boolean = true) {
        val estimatedPi = if (useStandardLib) RandomNumbersUtils.estimatePi(
            { (1L..32767L).random() }, // Lambda for standard random number generation
            totalPairs = numberOfPairs
        ) else RandomNumbersUtils.estimatePi(
            LehmerRandomNumberGenerator()::next, // Function reference to the Lehmer RNG's next() method
            totalPairs = numberOfPairs
        )
        _estimatedPi.postValue(estimatedPi)
    }
}