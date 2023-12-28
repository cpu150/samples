package com.example.common

fun Double.round(decimals: Int) = "%.${decimals}f".format(this).toDouble()
fun Float.round(decimals: Int) = "%.${decimals}f".format(this).toFloat()
