package com.dezdeqness.feed.core

import platform.Foundation.NSCalendar
import platform.Foundation.NSDate
import platform.Foundation.NSYearCalendarUnit

actual object DateUtils {
    actual fun getCurrentYear(): Int {
        val calendar = NSCalendar.currentCalendar
        val components = calendar.components(NSYearCalendarUnit, NSDate())
        return components.year.toInt()
    }
}
