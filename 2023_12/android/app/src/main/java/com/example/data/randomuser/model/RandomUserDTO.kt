package com.example.data.randomuser.model

import androidx.annotation.Keep
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class RandomUserDTO(
    @SerialName("name") val name: RandomUserNameDTO? = null,
    @SerialName("email") val email: String? = null,
    @SerialName("picture") val picture: RandomUserPictureDTO? = null,
    @SerialName("gender") val gender: String? = null,
)
