package com.example.common

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.time.DateTimeException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

class LocalDateTimeSerializer : KSerializer<LocalDateTime> {
    private val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

    override val descriptor =
        PrimitiveSerialDescriptor("LocalDateTime", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): LocalDateTime = try {
        LocalDateTime.parse(decoder.decodeString(), formatter)
    } catch (e: DateTimeParseException) {
        e.printStackTrace()
        LocalDateTime.MIN
    }

    override fun serialize(encoder: Encoder, value: LocalDateTime) {
        encoder.encodeString(
            try {
                value.format(formatter)
            } catch (e: DateTimeException) {
                e.printStackTrace()
                LocalDateTime.MIN.format(formatter)
            }
        )
    }
}
