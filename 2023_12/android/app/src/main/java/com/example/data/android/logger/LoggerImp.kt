package com.example.data.android.logger

import com.example.domain.Logger
import com.example.example2023.BuildConfig
import timber.log.Timber
import java.util.regex.Pattern

class LoggerImp : Logger {

    companion object {
        private val ANONYMOUS_CLASS = Pattern.compile("(\\$\\d+)+$")
    }

    private fun getStackTrace() = Throwable().stackTrace
        .firstOrNull { it.className != LoggerImp::class.java.name }
        ?: StackTraceElement("UnknownClass", "UnknownMethod", "UnknownFileName", -1)

    private fun getTag(stack: StackTraceElement): String {
        var tag = stack.className.substringAfterLast('.')
        val m = ANONYMOUS_CLASS.matcher(tag)
        if (m.find()) {
            tag = m.replaceAll("")
        }
        return tag
    }

    private fun getMessageFromStackTrace(
        message: String,
        t: Throwable,
    ) = with(mutableListOf(message)) {
        // print the stacktrace using one message per line to bypass the MESSAGE_MAX_LENGTH limit
        add(t.toString())
        addAll(t.stackTrace.toList().map { line -> line.toString() })
        toList()
    }

    override fun d(message: String) {
        if (BuildConfig.DEBUG) {
            val stack = getStackTrace()
            Timber.tag(getTag(stack)).d(message)
        }
    }

    override fun i(message: String) {
        val stack = getStackTrace()
        Timber.tag(getTag(stack)).i(message)
    }

    override fun w(message: String) {
        val stack = getStackTrace()
        Timber.tag(getTag(stack)).w(message)
    }

    override fun e(message: String, t: Throwable?) {
        val stack = getStackTrace()
        Timber.tag(getTag(stack)).e(t, message)
    }
}
