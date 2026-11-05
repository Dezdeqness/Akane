package com.dezdeqness.foundation.utils

fun Double.toEpisodeOrdinalString(): String =
    if (this % 1.0 == 0.0) {
        toLong().toString()
    } else {
        toString()
    }