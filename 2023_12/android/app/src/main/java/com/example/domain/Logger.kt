package com.example.domain

interface Logger {
    fun d(message: String)
    fun i(message: String)
    fun w(message: String)
    fun e(message: String, t: Throwable? = null)
}
