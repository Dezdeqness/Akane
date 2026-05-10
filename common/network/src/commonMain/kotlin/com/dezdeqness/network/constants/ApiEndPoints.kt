package com.dezdeqness.network.constants

object ApiEndPoints {
    private const val ANIME = "anime/"
    private const val ACCOUNTS_AUTH = "accounts/users/auth/"
    private const val ACCOUNTS_ME = "accounts/users/me/"

    const val PROFILE_ME = "${ACCOUNTS_ME}profile"
    const val CATALOG_RELEASES = "${ANIME}catalog/releases"
    const val RELEASE = "${ANIME}releases/{id}"
    const val FRANCHISE = "${ANIME}franchises/release/{id}"
    const val SCHEDULE_NOW = "${ANIME}schedule/now"

    const val AUTH_LOGIN = "${ACCOUNTS_AUTH}login"
    const val AUTH_LOGOUT = "${ACCOUNTS_AUTH}logout"
    const val AUTH_REGISTRATION = "${ACCOUNTS_AUTH}registration/new"
    const val AUTH_PASSWORD_FORGET = "${ACCOUNTS_AUTH}password/forget"
    const val AUTH_PASSWORD_RESET = "${ACCOUNTS_AUTH}password/reset"
}
