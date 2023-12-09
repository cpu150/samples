package com.example.data.api.randomuser.model

import androidx.annotation.Keep
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/*
 * Here there are redundancies regarding Proguard annotations and rules:
 *   - @Keep: keeps class and class member names
 *   - @SerialName: keeps class member names
 *   - Proguard: `-keepclassmembers class com.example.data.randomuser.model.** { *; }`
 */
@Keep
@Serializable
data class GetRandomUsersDTO(
    @SerialName("results") val results: List<RandomUserDTO>? = null,
    @SerialName("info") val info: InfoRandomUserDTO? = null,
)
