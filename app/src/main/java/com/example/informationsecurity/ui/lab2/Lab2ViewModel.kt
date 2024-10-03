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

    fun md5(input: ByteArray) {
        _output.apply {
            value = md.digest(input).joinToString(" ") {
                "%02x".format(it)
            }
        }
    }
}