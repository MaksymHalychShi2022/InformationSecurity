package com.example.informationsecurity.ui.lab2

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.security.MessageDigest

class Lab2ViewModel : ViewModel() {
    private val md = MessageDigest.getInstance("MD5")

    private val _output = MutableLiveData<String>().apply {
        value = "Output md hash will be here!"
    }
    val output: LiveData<String> = _output

    fun md5(input: String) {
        // Compute the hash in bytes
        val digestBytes = md.digest(input.toByteArray())

        // Convert the bytes to a hexadecimal string
        _output.apply {
            value = digestBytes.joinToString(" ") {
                "%02x".format(it)
            }
        }
    }
}