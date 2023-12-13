package com.example.domain.model

enum class UserTitle(val value: String) {
    UNKNOWN("UNKNOWN"),
    MS("Ms"),
    MISS("Miss"),
    MR("Mr"),
    MRS("Mrs");

    companion object {
        fun from(str: String?) = entries.find { it.value == str } ?: UNKNOWN
    }
}
