package com.example.domain.model

import com.example.domain.Logger

enum class UserTitle(val entityValue: String) {
    UNKNOWN("UNKNOWN"),
    MS("Ms"),
    MISS("Miss"),
    MR("Mr"),
    MRS("Mrs");

    companion object {

        fun fromEntity(
            entityValue: String?,
            logger: Logger? = null,
        ) = entries.find { it.entityValue == entityValue }
            ?: let {
                logger?.e("UserTitle - Error parsing $entityValue")
                UNKNOWN
            }
    }
}
