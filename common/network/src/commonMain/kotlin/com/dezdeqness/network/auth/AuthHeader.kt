package com.dezdeqness.network.auth

object AuthHeader {
    const val NAME = "X-Requires-Auth"
    const val VALUE = "true"
    const val REQUIRES_AUTH = "$NAME: $VALUE"
}
