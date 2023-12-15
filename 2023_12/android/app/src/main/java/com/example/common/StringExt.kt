package com.example.common

import com.example.domain.Logger
import java.net.URL
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun String.toDateTime(format: String, logger: Logger? = null) = try {
    LocalDateTime.parse(this, DateTimeFormatter.ofPattern(format))
} catch (e: Exception) {
    logger?.e("Error parsing date $this", e)
    null
}

fun String.toURL(logger: Logger? = null) = try {
    URL(this)
} catch (e: Exception) {
    logger?.e("Error parsing url $this", e)
    null
}
