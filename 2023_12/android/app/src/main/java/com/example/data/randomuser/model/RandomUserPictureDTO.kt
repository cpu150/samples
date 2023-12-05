package com.example.data.randomuser.model

import androidx.annotation.Keep
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class RandomUserPictureDTO(
    @SerialName("large") val large: String? = null,
    @SerialName("medium") val medium: String? = null,
    @SerialName("thumbnail") val thumbnail: String? = null,
)
