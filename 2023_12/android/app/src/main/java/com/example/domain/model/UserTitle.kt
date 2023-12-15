package com.example.domain.model

import com.example.domain.Logger

enum class UserTitle(val value: String) {
    UNKNOWN("UNKNOWN"),
    MS("Ms"),
    MISS("Miss"),
    MR("Mr"),
    MRS("Mrs");

    companion object {

        fun from(
            str: String?,
            logger: Logger? = null,
        ) = entries.find { it.value == str }
            ?: let {
                logger?.e("UserTitle - Error parsing $str")
                UNKNOWN
            }
    }
}
