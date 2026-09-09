package com.dezdeqness.screenshot

enum class Viewport(
    val label: String,
    val widthPx: Int,
    val heightPx: Int,
    val columns: Int,
) {
    Mobile("mobile", widthPx = 400, heightPx = 880, columns = 3),
    Tablet("tablet", widthPx = 960, heightPx = 540, columns = 5),
    Desktop("desktop", widthPx = 1440, heightPx = 900, columns = 6),
}
