package com.example.common

import android.net.Uri
import com.example.domain.Logger
import java.net.MalformedURLException
import java.net.URL
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun String.toDateTime(format: String, logger: Logger? = null) = try {
    LocalDateTime.parse(this, DateTimeFormatter.ofPattern(format))
} catch (e: IllegalArgumentException) {
    logger?.e("Error parsing date $this", e)
    null
}

fun String.toURL(logger: Logger? = null) = try {
    URL(this)
} catch (e: MalformedURLException) {
    logger?.e("Error parsing URL $this", e)
    null
}

fun String.toUri(logger: Logger? = null) = try {
    Uri.parse(this)
} catch (e: NullPointerException) {
    logger?.e("Error parsing Uri $this", e)
    null
}
