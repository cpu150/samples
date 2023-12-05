package com.example.data.randomuser.model

import androidx.annotation.Keep
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class GetRandomUsersDTO(
    @SerialName("results") val results: List<RandomUserDTO>? = null,
    @SerialName("info") val info: InfoRandomUserDTO? = null,
)
