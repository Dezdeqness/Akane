package com.dezdeqness.analytics.utils

import com.dezdeqness.analytics.core.AnalyticsValue

fun String.asAnalyticsValue(): AnalyticsValue = AnalyticsValue.Text(this)

fun Number.asAnalyticsValue(): AnalyticsValue = AnalyticsValue.Numeric(this)
