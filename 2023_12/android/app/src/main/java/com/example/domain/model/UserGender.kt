package com.example.domain.model

import com.example.data.storage.user.UserTable.columnGenderDefaultValue
import com.example.domain.Logger

enum class UserGender(val entityValue: String) {
    UNKNOWN(columnGenderDefaultValue),
    MALE("male"),
    FEMALE("female");

    companion object {
        fun fromEntity(
            entityValue: String?,
            logger: Logger? = null,
        ) = entries.find { it.entityValue == entityValue }
            ?: let {
                logger?.e("UserGender - Enable to parse $entityValue")
                UNKNOWN
            }
    }
}
