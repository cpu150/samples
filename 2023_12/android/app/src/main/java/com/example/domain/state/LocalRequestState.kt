package com.example.domain.state

sealed interface LocalRequestState<out T> {
    data class Create<T>(val data: T) : LocalRequestState<T>
    data class Read<T>(val data: T) : LocalRequestState<T>
    data class Update<T>(val data: T) : LocalRequestState<T>
    data class Delete<T>(val data: T) : LocalRequestState<T>

    data class ErrorCreate<T>(val data: T, val e: Exception? = null) : LocalRequestState<T>
    data class ErrorRead(val e: Exception? = null) : LocalRequestState<Nothing>
    data class ErrorUpdate<T>(val data: T, val e: Exception? = null) : LocalRequestState<T>
    data class ErrorDelete<T>(val data: T, val e: Exception? = null) : LocalRequestState<T>
}
