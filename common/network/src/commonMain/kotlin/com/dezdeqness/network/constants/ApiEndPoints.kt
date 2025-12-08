package com.dezdeqness.network.constants

object ApiEndPoints {
    private const val ANIME = "anime/"
    const val CATALOG_RELEASES = "${ANIME}catalog/releases"
    const val RELEASE = "${ANIME}releases/{id}"
    const val FRANCHISE = "${ANIME}franchises/release/{id}"
    const val SCHEDULE_NOW = "${ANIME}schedule/now"
}
