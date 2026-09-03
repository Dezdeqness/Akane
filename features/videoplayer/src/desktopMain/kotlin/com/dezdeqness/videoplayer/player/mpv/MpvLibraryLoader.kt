package com.dezdeqness.videoplayer.player.mpv

import com.sun.jna.Library
import com.sun.jna.Native
import com.sun.jna.Platform
import java.io.File
import java.net.URL
import java.net.URLDecoder
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.util.jar.JarFile

/**
 * Loads libmpv that is bundled *inside* the app — no system-wide mpv/VLC install needed.
 *
 * Place the native library in desktop resources following JNA's own resource-prefix
 * convention ([Platform.RESOURCE_PREFIX]), e.g.:
 *
 *   features/videoplayer/src/desktopMain/resources/mpv/win32-x86-64/libmpv-2.dll
 *   features/videoplayer/src/desktopMain/resources/mpv/darwin-aarch64/libmpv.2.dylib
 *
 * On Windows the shinchiro build is a single self-contained DLL. On macOS the build is
 * `libmpv.2.dylib` plus its dependency dylibs (linked via `@loader_path/`), so the whole
 * folder is extracted together and the loader points JNA at the main library.
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
     * Extracts the bundled library (and, on macOS, its sibling dependency dylibs) into a
     * persistent cache dir and returns the absolute path of the main library.
     *
     * Every file under `/mpv/<prefix>/` is extracted next to the main library so that
     * `@loader_path`-relative dependencies resolve. The copy happens only on the first
     * launch (or after an update): a file is reused when its size matches the bundled one.
     */
    private fun extractBundled(): String? {
        val prefix = Platform.RESOURCE_PREFIX // e.g. win32-x86-64, darwin-aarch64
        val primary = candidates.firstOrNull { javaClass.getResource("/mpv/$prefix/$it") != null }
            ?: return null
        val primaryUrl = javaClass.getResource("/mpv/$prefix/$primary") ?: return null

        val cacheDir = File(System.getProperty("user.home"), ".akane/runtime/mpv/$prefix")
        cacheDir.mkdirs()

        for (name in siblingNames(primaryUrl, "mpv/$prefix", primary)) {
            val url = javaClass.getResource("/mpv/$prefix/$name") ?: continue
            val out = File(cacheDir, name)
            val expectedSize = runCatching { url.openConnection().contentLengthLong }.getOrDefault(-1L)
            if (out.isFile && expectedSize >= 0 && out.length() == expectedSize) continue // up-to-date
            url.openStream().use { input ->
                Files.copy(input, out.toPath(), StandardCopyOption.REPLACE_EXISTING)
            }
        }
        return File(cacheDir, primary).absolutePath
    }

    private fun siblingNames(primaryUrl: URL, dir: String, primary: String): List<String> {
        val found = when (primaryUrl.protocol) {
            "file" ->
                File(primaryUrl.toURI()).parentFile
                    ?.listFiles()
                    ?.filter { it.isFile }
                    ?.map { it.name }
                    .orEmpty()

            "jar" -> {
                val jarPath = primaryUrl.path.substringAfter("file:").substringBefore("!")
                JarFile(URLDecoder.decode(jarPath, "UTF-8")).use { jar ->
                    jar.entries().asSequence()
                        .map { it.name }
                        .filter { it.startsWith("$dir/") && !it.endsWith("/") }
                        .map { it.removePrefix("$dir/") }
                        .filter { it.isNotEmpty() && !it.contains('/') }
                        .toList()
                }
            }

            else -> emptyList()
        }
        return found.ifEmpty { listOf(primary) }
    }

    /** Warms the native library on a background thread (e.g. at app startup / splash). */
    fun preloadAsync() {
        if (cached != null) return
        Thread({ runCatching { load() } }, "mpv-preload").apply {
            isDaemon = true
            start()
        }
    }

    fun ensureCNumericLocale() {
        runCatching {
            val libc = Native.load(if (Platform.isWindows()) "msvcrt" else "c", PosixC::class.java)
            val lcNumeric = if (Platform.isLinux()) 1 else 4 // glibc = 1, macOS/Windows = 4
            libc.setlocale(lcNumeric, "C")
        }
    }
}

private interface PosixC : Library {
    fun setlocale(category: Int, locale: String?): String?
}

internal class MpvNotBundledException(message: String) : RuntimeException(message)
