package com.example.data.randomuser.model

import androidx.annotation.Keep
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class RandomUserNameDTO(
    @SerialName("title") val title: String? = null,
    @SerialName("first") val firstName: String? = null,
    @SerialName("last") val lastName: String? = null,
)
