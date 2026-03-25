package com.dezdeqness.downloads.data.hls

class HlsParser {

    data class HlsPlaylist(
        val segments: List<String>,
        val segmentDurations: List<Double>,
        val targetDuration: Int,
    )

    fun parse(m3u8Content: String, baseUrl: String): HlsPlaylist {
        val lines = m3u8Content.lines().map { it.trim() }
        val segments = mutableListOf<String>()
        val durations = mutableListOf<Double>()
        var targetDuration = 10

        for (i in lines.indices) {
            val line = lines[i]
            if (line.startsWith("#EXT-X-TARGETDURATION:")) {
                targetDuration = line.substringAfter(":").toIntOrNull() ?: 10
            } else if (line.startsWith("#EXTINF:")) {
                val duration = line.substringAfter(":").substringBefore(",").toDoubleOrNull() ?: 10.0
                durations.add(duration)
            } else if (line.isNotBlank() && !line.startsWith("#")) {
                val url = if (line.startsWith("http")) line else resolveRelativeUrl(baseUrl, line)
                segments.add(url)
                if (durations.size < segments.size) {
                    durations.add(10.0)
                }
            }
        }

        return HlsPlaylist(
            segments = segments,
            segmentDurations = durations,
            targetDuration = targetDuration,
        )
    }

    private fun resolveRelativeUrl(baseUrl: String, relative: String): String {
        val base = baseUrl.substringBeforeLast("/")
        return "$base/$relative"
    }
}
