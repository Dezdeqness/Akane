@file:OptIn(ExperimentalTime::class)

package com.dezdeqness.personal.core

import kotlin.time.Clock
import kotlin.time.ExperimentalTime

actual fun currentTimeMillis(): Long {
    return Clock.System.now().toEpochMilliseconds()
}