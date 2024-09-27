package com.example.data.storage

import androidx.room.TypeConverter
import java.time.DateTimeException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

class TimestampConverter {

    companion object {
        private const val DATE_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS"
        private val df = DateTimeFormatter.ofPattern(DATE_PATTERN)
    }

    @TypeConverter
    fun fromTimestamp(value: String?): LocalDateTime? = value?.let {
        try {
            LocalDateTime.from(df.parse(it))
        } catch (e: DateTimeParseException) {
            e.printStackTrace()
            null
        }
    }

    @TypeConverter
    fun toTimestamp(value: LocalDateTime?): String? = value?.let {
        try {
            df.format(it)
        } catch (e: DateTimeException) {
            e.printStackTrace()
            null
        }
    }
}
