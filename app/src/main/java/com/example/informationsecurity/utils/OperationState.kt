package com.example.informationsecurity.utils

sealed class OperationState<T>(
    val data: T? = null,
    val message: String? = null
) {
    class Success<T>(data: T) : OperationState<T>(data)
    class Error<T>(message: String, data: T? = null) : OperationState<T>(data, message)
    class Loading<T> : OperationState<T>()
}