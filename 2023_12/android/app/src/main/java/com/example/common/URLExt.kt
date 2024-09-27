package com.example.common

import android.net.Uri
import java.net.URL

fun URL.toUri() = try {
    Uri.parse(toURI().toString())
} catch (e: NullPointerException) {
    e.printStackTrace()
    null
}
