package com.example.informationsecurity.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {

    private val _isVisibleProgressBar = MutableLiveData<Boolean>().apply {
        value = false
    }
    val isVisibleProgressBar: LiveData<Boolean> = _isVisibleProgressBar

    fun showProgressBar() {
        _isVisibleProgressBar.apply {
            value = true
        }
    }

    fun hideProgressBar() {
        _isVisibleProgressBar.apply {
            value = false
        }
    }
}