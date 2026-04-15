package com.dezdeqness.analytics

import com.dezdeqness.analytics.core.AkaneErrorReporter
import com.dezdeqness.analytics.core.ErrorLevel
import io.sentry.kotlin.multiplatform.Sentry
import io.sentry.kotlin.multiplatform.SentryLevel

class SentryAkaneErrorReporter : AkaneErrorReporter {
    override fun captureException(
        throwable: Throwable,
        message: String?,
        level: ErrorLevel,
        tags: Map<String, String>,
        extras: Map<String, String>,
    ) {
        Sentry.captureException(throwable) { scope ->
            scope.level = level.toSentryLevel()
            message?.let { scope.setExtra("message", it) }
            tags.forEach { (key, value) -> scope.setTag(key, value) }
            extras.forEach { (key, value) -> scope.setExtra(key, value) }
        }
    }

    override fun captureMessage(
        message: String,
        level: ErrorLevel,
        tags: Map<String, String>,
        extras: Map<String, String>,
    ) {
        Sentry.captureMessage(message) { scope ->
            scope.level = level.toSentryLevel()
            tags.forEach { (key, value) -> scope.setTag(key, value) }
            extras.forEach { (key, value) -> scope.setExtra(key, value) }
        }
    }
}

private fun ErrorLevel.toSentryLevel(): SentryLevel = when (this) {
    ErrorLevel.DEBUG -> SentryLevel.DEBUG
    ErrorLevel.INFO -> SentryLevel.INFO
    ErrorLevel.WARNING -> SentryLevel.WARNING
    ErrorLevel.ERROR -> SentryLevel.ERROR
    ErrorLevel.FATAL -> SentryLevel.FATAL
}
