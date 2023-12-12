package com.example.domain.state

sealed interface RemoteRequestState<out T> {
    data class Success<T>(val data: T) : RemoteRequestState<T>
    data object Empty : RemoteRequestState<Nothing>
    data class Error(
        val reason: ReasonCode = ReasonCode.UNKNOWN,
        val httpCode: Int? = null,
        val msg: String? = null,
        val ex: Exception? = null,
    ) : RemoteRequestState<Nothing>

    enum class ReasonCode {
        UNKNOWN,
        API_REQUEST,
        BODY_NULL,
        ERROR_BODY_NULL,
        ERROR_BODY_DESERIALIZATION,
    }
}
