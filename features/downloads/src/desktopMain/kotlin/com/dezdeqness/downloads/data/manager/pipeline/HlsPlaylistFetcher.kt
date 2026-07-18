package com.dezdeqness.downloads.data.manager.pipeline

import co.touchlab.kermit.Logger
import com.dezdeqness.downloads.data.hls.HlsParser
import com.dezdeqness.downloads.data.network.HlsDownloadService
import kotlinx.coroutines.CancellationException

class HlsPlaylistFetcher(
    private val hlsDownloadService: HlsDownloadService,
    private val hlsParser: HlsParser,
) {

    suspend fun fetch(downloadId: Long, hlsUrl: String): HlsParser.HlsPlaylist {
        Logger.d(TAG) { "[$downloadId] Fetching m3u8: $hlsUrl" }

        val m3u8Content = try {
            hlsDownloadService.fetchM3u8(hlsUrl)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Logger.e(TAG, e) { "[$downloadId] Failed to fetch m3u8: url=$hlsUrl" }
            throw e
        }

        Logger.d(TAG) { "[$downloadId] m3u8 content length=${m3u8Content.length}" }

        val playlist = try {
            hlsParser.parse(m3u8Content, hlsUrl)
        } catch (e: Exception) {
            Logger.e(TAG, e) {
                "[$downloadId] Failed to parse m3u8: url=$hlsUrl, content:\n${m3u8Content.take(500)}"
            }
            throw e
        }

        Logger.d(TAG) { "[$downloadId] Parsed ${playlist.segments.size} segments" }

        if (playlist.segments.isEmpty()) {
            Logger.e(TAG) { "[$downloadId] No segments found in m3u8" }
            throw IllegalStateException("No segments found in m3u8 playlist")
        }

        return playlist
    }

    companion object {
        private const val TAG = "HlsPlaylistFetcher"
    }
}
