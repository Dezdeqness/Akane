package com.dezdeqness.akane

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform