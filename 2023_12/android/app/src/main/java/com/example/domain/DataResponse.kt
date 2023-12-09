package com.example.domain

sealed interface DataResponse<out T> {
    data class Success<T>(val data: T) : DataResponse<T>
    data object Empty : DataResponse<Nothing>
    data class Error(
        val reason: ReasonCode = ReasonCode.UNKNOWN,
        val httpCode: Int? = null,
        val msg: String? = null,
        val ex: Exception? = null,
    ) : DataResponse<Nothing>

    enum class ReasonCode {
        UNKNOWN,
        API_REQUEST,
        BODY_NULL,
        ERROR_BODY_NULL,
        ERROR_BODY_DESERIALIZATION,
    }
}
