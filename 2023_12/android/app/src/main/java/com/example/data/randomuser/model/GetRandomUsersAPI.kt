package com.example.data.randomuser.model

import androidx.annotation.Keep
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class GetRandomUsersAPI(
    @SerialName("results") val results: List<RandomUserAPI>? = null,
    @SerialName("info") val info: InfoRandomUserAPI? = null,
)
