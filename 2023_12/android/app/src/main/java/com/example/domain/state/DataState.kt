package com.example.domain.state

sealed interface DataState<out T> {
    data class Success<T>(val data: T) : DataState<T>
    data object Empty : DataState<Nothing>
    data class Error(
        val reason: ReasonCode = ReasonCode.UNKNOWN,
        val httpCode: Int? = null,
        val msg: String? = null,
        val ex: Exception? = null,
    ) : DataState<Nothing>

    enum class ReasonCode {
        UNKNOWN,
        API_REQUEST,
        BODY_NULL,
        ERROR_BODY_NULL,
        ERROR_BODY_DESERIALIZATION,
    }
}
