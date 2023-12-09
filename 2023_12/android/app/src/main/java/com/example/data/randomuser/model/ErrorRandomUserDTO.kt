package com.example.data.randomuser.model

import androidx.annotation.Keep
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class ErrorRandomUserDTO(
    @SerialName("error") val error: String? = null,
)
