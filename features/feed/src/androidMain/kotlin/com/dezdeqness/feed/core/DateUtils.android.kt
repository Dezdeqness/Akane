package com.dezdeqness.feed.core

import java.util.Calendar

actual object DateUtils {
    actual fun getCurrentYear(): Int {
        return Calendar.getInstance().get(Calendar.YEAR)
    }
}
