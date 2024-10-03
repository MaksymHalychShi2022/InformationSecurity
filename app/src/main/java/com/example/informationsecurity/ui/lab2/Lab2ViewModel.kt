package com.example.informationsecurity.ui.lab2

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.informationsecurity.utils.OperationState
import kotlinx.coroutines.launch
import java.security.MessageDigest

class Lab2ViewModel : ViewModel() {
    private val md = MessageDigest.getInstance("MD5")

    private val _output = MutableLiveData<String>().apply {
        value = "Output md hash will be here!"
    }
    val output: LiveData<String> = _output

    fun md5(input: String): LiveData<OperationState<Unit>> {
        val operationState = MutableLiveData<OperationState<Unit>>()
        viewModelScope.launch {
            operationState.postValue(OperationState.Loading())
            try {
                val hash = md.digest(input.toByteArray()).joinToString("") {
                    "%02x".format(it)
                }
                _output.apply {
                    value = hash
                }
                operationState.postValue(OperationState.Success(Unit))
            } catch (e: Exception) {
                operationState.postValue(OperationState.Error("Unknown error"))
            }
        }
        return operationState
    }
}