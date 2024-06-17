package com.secure.jnet.wallet.domain.models

sealed class Result<out T> {
    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val throwable: Throwable) : Result<Nothing>(){
        constructor(error: Result<*>) : this((error as Error).throwable)
    }

    fun asSuccess(): Success<T> {
        return this as Success
    }
}