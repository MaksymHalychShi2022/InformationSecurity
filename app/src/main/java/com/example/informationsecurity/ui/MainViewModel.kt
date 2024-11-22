package com.example.informationsecurity.ui

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

open class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val _isVisibleProgressBar = MutableLiveData<Boolean>().apply {
        value = false
    }
    val isVisibleProgressBar: LiveData<Boolean> = _isVisibleProgressBar

    fun <T> runWithProgress(
        task: suspend () -> T,
        onSuccess: ((T) -> Unit)? = null,
        onSuccessMessage: String? = null,
        onError: ((Throwable) -> Unit)? = {
            Toast.makeText(
                getApplication(),
                "Error: ${it.message}",
                Toast.LENGTH_LONG
            ).show()
        }
    ) {
        _isVisibleProgressBar.value = true  // Show progress bar
        viewModelScope.launch {
            Log.d("ViewBinding", "ProgressBar: ${_isVisibleProgressBar.value}")
            try {
                val result = task() // Execute the suspend function
                onSuccessMessage?.let {
                    Toast.makeText(getApplication(), it, Toast.LENGTH_SHORT).show()
                }
                onSuccess?.invoke(result) // Call onSuccess if provided
            } catch (e: Throwable) {
                onError?.invoke(e) // Call onError if provided
            } finally {

            }
            _isVisibleProgressBar.value = false // Hide progress bar
            Log.d("ViewBinding", "ProgressBar: ${_isVisibleProgressBar.value}")
        }
    }
}