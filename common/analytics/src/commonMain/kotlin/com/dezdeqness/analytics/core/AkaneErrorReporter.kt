package com.dezdeqness.analytics.core

interface AkaneErrorReporter {
    fun captureException(
        throwable: Throwable,
        message: String? = null,
        level: ErrorLevel = ErrorLevel.ERROR,
        tags: Map<String, String> = emptyMap(),
        extras: Map<String, String> = emptyMap(),
    )

    fun captureMessage(
        message: String,
        level: ErrorLevel = ErrorLevel.ERROR,
        tags: Map<String, String> = emptyMap(),
        extras: Map<String, String> = emptyMap(),
    )
}

enum class ErrorLevel {
    DEBUG,
    INFO,
    WARNING,
    ERROR,
    FATAL,
}
