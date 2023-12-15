package com.example.data.storage

import androidx.room.TypeConverter
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class TimestampConverter {

    companion object {
        private const val datePattern = "yyyy-MM-dd'T'HH:mm:ss.SSS"
        private val df = DateTimeFormatter.ofPattern(datePattern)
    }

    @TypeConverter
    fun fromTimestamp(value: String?): LocalDateTime? = value?.let {
        try {
            LocalDateTime.from(df.parse(it))
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    @TypeConverter
    fun toTimestamp(value: LocalDateTime?): String? = value?.let {
        try {
            df.format(it)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
