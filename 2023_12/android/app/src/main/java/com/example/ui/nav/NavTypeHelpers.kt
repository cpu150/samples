package com.example.ui.nav

import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavType
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

interface NavArgParser<T> {
    fun from(savedStateHandle: SavedStateHandle, json: Json = Json): T
}

// Encode '/' characters as it will be miss interpreted (as a separator) by the NavController
fun fixUrlForNavigation(urlStr: String) = urlStr
    .replace("/", "%2F")

inline fun <reified T : Parcelable> parcelableType(
    isNullableAllowed: Boolean = false,
    json: Json = Json,
) = object : NavType<T>(isNullableAllowed = isNullableAllowed) {

    override fun get(
        bundle: Bundle,
        key: String
    ) = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        bundle.getParcelable(key, T::class.java)
    } else {
        @Suppress("DEPRECATION")
        bundle.getParcelable(key)
    }

    override fun parseValue(value: String): T = json.decodeFromString(value)

    override fun serializeAsValue(value: T): String =
        fixUrlForNavigation(json.encodeToString(value))

    override fun put(bundle: Bundle, key: String, value: T) = bundle.putParcelable(key, value)
}

inline fun <reified T : Any> serializableType(
    isNullableAllowed: Boolean = false,
    json: Json = Json,
) = object : NavType<T>(isNullableAllowed = isNullableAllowed) {

    override fun get(bundle: Bundle, key: String) =
        bundle.getString(key)?.let<String, T>(json::decodeFromString)

    override fun parseValue(value: String): T = json.decodeFromString(value)

    override fun serializeAsValue(value: T): String =
        fixUrlForNavigation(json.encodeToString(value))

    override fun put(bundle: Bundle, key: String, value: T) =
        bundle.putString(key, json.encodeToString(value))
}
