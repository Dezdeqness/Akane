package com.dezdeqness.videoplayer.player.mpv

import com.sun.jna.Library
import com.sun.jna.Native
import com.sun.jna.Platform
import java.io.File
import java.nio.file.Files
import java.nio.file.StandardCopyOption

/**
 * Loads libmpv that is bundled *inside* the app — no system-wide mpv/VLC install needed.
 *
 * Place the self-contained native library in desktop resources following JNA's own
 * resource-prefix convention ([Platform.RESOURCE_PREFIX]), e.g.:
 *
 *   features/videoplayer/src/desktopMain/resources/mpv/win32-x86-64/libmpv-2.dll
 *   features/videoplayer/src/desktopMain/resources/mpv/darwin-aarch64/libmpv.2.dylib
 *   features/videoplayer/src/desktopMain/resources/mpv/darwin-x86-64/libmpv.2.dylib
 *
 * Unlike libVLC, libmpv is a single self-contained file (ffmpeg is linked in),
 * so bundling is just dropping one file per platform — no plugins directory.
 */
internal object MpvLibraryLoader {

    @Volatile
    private var cached: LibMpv? = null

    private val candidates: List<String>
        get() = when {
            Platform.isWindows() -> listOf("libmpv-2.dll", "mpv-2.dll", "libmpv.dll")
            Platform.isMac() -> listOf("libmpv.2.dylib", "libmpv.dylib")
            else -> listOf("libmpv.so.2", "libmpv.so")
        }

    @Synchronized
    fun load(): LibMpv {
        cached?.let { return it }

        val absolutePath = extractBundled()
            ?: throw MpvNotBundledException(
                "libmpv not found in resources under /mpv/${Platform.RESOURCE_PREFIX}/. " +
                    "Expected one of: ${candidates.joinToString()}"
            )

        val options = mapOf(Library.OPTION_STRING_ENCODING to "UTF-8")
        val lib = Native.load(absolutePath, LibMpv::class.java, options)
        cached = lib
        return lib
    }

    /**
     * Extracts the bundled library into a persistent cache dir and returns its path.
     * The ~120 MB copy happens only on the first launch (or after an update): on later
     * launches the already-extracted file is reused when its size matches the bundled one.
     */
    private fun extractBundled(): String? {
        val prefix = Platform.RESOURCE_PREFIX
        for (name in candidates) {
            val resourcePath = "/mpv/$prefix/$name"
            val url = javaClass.getResource(resourcePath) ?: continue

            val cacheDir = File(System.getProperty("user.home"), ".akane/runtime/mpv/$prefix")
            cacheDir.mkdirs()
            val out = File(cacheDir, name)

            val expectedSize = runCatching { url.openConnection().contentLengthLong }.getOrDefault(-1L)
            if (out.isFile && expectedSize >= 0 && out.length() == expectedSize) {
                return out.absolutePath
            }

            url.openStream().use { input ->
                Files.copy(input, out.toPath(), StandardCopyOption.REPLACE_EXISTING)
            }
            return out.absolutePath
        }
        return null
    }

    /** Warms the native library on a background thread (e.g. at app startup / splash). */
    fun preloadAsync() {
        if (cached != null) return
        Thread({ runCatching { load() } }, "mpv-preload").apply {
            isDaemon = true
            start()
        }
    }
}

internal class MpvNotBundledException(message: String) : RuntimeException(message)
