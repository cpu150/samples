package com.example.data.api.randomuser.model

import androidx.annotation.Keep
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class DobDTO(
    @SerialName("date") val date: String? = null,
    @SerialName("age") val age: Int? = null,
) {
    companion object {
        // ex: 1963-10-25T13:56:32.813Z
        const val birthDateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSSZZZZZ"
    }
}
