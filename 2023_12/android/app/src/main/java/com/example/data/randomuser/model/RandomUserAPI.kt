package com.example.data.randomuser.model

import androidx.annotation.Keep
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class RandomUserAPI(
    @SerialName("name") val name: RandomUserNameAPI? = null,
    @SerialName("email") val email: String? = null,
    @SerialName("picture") val picture: RandomUserPictureAPI? = null,
    @SerialName("gender") val gender: String? = null,
)
