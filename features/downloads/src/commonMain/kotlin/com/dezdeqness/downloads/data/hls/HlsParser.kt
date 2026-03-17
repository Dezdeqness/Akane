package com.dezdeqness.downloads.data.hls

class HlsParser {

    data class HlsPlaylist(
        val segments: List<String>,
    )

    fun parse(m3u8Content: String, baseUrl: String): HlsPlaylist {
        val segments = m3u8Content.lines()
            .map { it.trim() }
            .filter { it.isNotBlank() && !it.startsWith("#") }
            .map { line ->
                if (line.startsWith("http")) line
                else resolveRelativeUrl(baseUrl, line)
            }
        return HlsPlaylist(segments = segments)
    }

    private fun resolveRelativeUrl(baseUrl: String, relative: String): String {
        val base = baseUrl.substringBeforeLast("/")
        return "$base/$relative"
    }
}
