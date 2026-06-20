package com.dezdeqness.network.constants

class ImageUrlBuilder {

    fun build(path: String?): String = when {
        path.isNullOrBlank() -> ""
        path.startsWith("http://") || path.startsWith("https://") -> path
        else -> BaseUrl.BASE_URL_IMAGES + path
    }
}
