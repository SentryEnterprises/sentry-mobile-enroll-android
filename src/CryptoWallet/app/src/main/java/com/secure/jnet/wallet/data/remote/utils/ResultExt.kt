package com.secure.jnet.wallet.data.remote.utils

import com.secure.jnet.wallet.domain.models.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import retrofit2.Response

suspend fun <R> CoroutineDispatcher.makeRequest(action: suspend () -> R): Result<R> {
    return try {
        withContext(this) {
            val result = action()
            if (result is Response<*>) {
                if (result.isSuccessful) {
                    Result.Success(result)
                } else {
                    Result.Error(HttpException(result))
                }
            } else {
                Result.Success(result)
            }
        }
    } catch (exception: Exception) {
        Result.Error(exception)
    }
}

fun <R> wrapResult(action: () -> R): Result<R> {
    return try {
        val result = action()
        Result.Success(result)
    } catch (exception: Exception) {
        Result.Error(exception)
    }
}
