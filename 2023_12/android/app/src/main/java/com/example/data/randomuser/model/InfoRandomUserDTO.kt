package com.example.data.randomuser.model

import androidx.annotation.Keep
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class InfoRandomUserDTO(
    @SerialName("seed") val seed: String? = null,
    @SerialName("results") val results: Int? = null,
    @SerialName("page") val page: Int? = null,
    @SerialName("version") val version: String? = null,
)
