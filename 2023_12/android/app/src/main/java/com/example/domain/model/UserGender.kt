package com.example.domain.model

import com.example.domain.Logger

enum class UserGender(val value: String) {
    UNKNOWN("UNKNOWN"),
    MALE("male"),
    FEMALE("female");

    companion object {
        fun from(
            str: String?,
            logger: Logger? = null,
        ) = entries.find { it.value == str }
            ?: let {
                logger?.e("UserGender - Enable to parse $str")
                UNKNOWN
            }
    }
}
