package com.dezdeqness.downloads.data.manager

import co.touchlab.kermit.Logger
import com.dezdeqness.downloads.data.platform.DownloadDirectoryProvider
import com.dezdeqness.downloads.contract.model.DownloadEntity
import okio.BufferedSink
import okio.FileSystem
import okio.Path
import okio.Path.Companion.toPath
import okio.SYSTEM
import okio.buffer

class DownloadFileManager(
    private val downloadDirectoryProvider: DownloadDirectoryProvider,
    private val fileSystem: FileSystem = FileSystem.SYSTEM,
) {

    fun toRelativePath(absolutePath: String): String {
        val normalizedRoot = normalizePath(downloadDirectoryProvider.getDownloadDirectory())
        val normalizedPath = normalizePath(absolutePath)

        return when {
            normalizedPath == normalizedRoot -> ""
            normalizedPath.startsWith("$normalizedRoot/") -> {
                normalizedPath.removePrefix("$normalizedRoot/")
            }
            else -> absolutePath
        }
    }

    fun resolveFilePath(relativePath: String): String {
        if (relativePath.startsWith("http://") || relativePath.startsWith("https://")) return relativePath
        if (relativePath.startsWith("bookmark://")) return relativePath
        if (isAbsolutePath(relativePath)) return normalizePath(relativePath)

        val root = normalizePath(downloadDirectoryProvider.getDownloadDirectory())
        val relative = relativePath.removePrefix("/").removePrefix("\\")
        return "$root/$relative"
    }

    fun getOutputPath(download: DownloadEntity): Path {
        val releaseFolder = getReleaseFolder(download)
        ensureDirectory(releaseFolder)

        val fileName = buildString {
            append(download.episodeOrdinal)
            append("_")
            append(sanitize(download.episodeName))
            append("_")
            append(download.quality)
            append(".ts")
        }

        return releaseFolder / fileName
    }

    fun getSegmentsDir(download: DownloadEntity): Path {
        val segmentsDir = buildSegmentsDir(download)
        ensureDirectory(segmentsDir)
        return segmentsDir
    }

    fun getSegmentPath(segmentsDir: Path, index: Int): Path =
        segmentsDir / "segment_$index.ts"

    fun getTempSegmentPath(segmentsDir: Path, index: Int): Path =
        segmentsDir / "segment_$index.ts.part"

    fun fileExists(path: Path): Boolean = fileSystem.exists(path)

    fun segmentExists(path: Path): Boolean {
        if (!fileSystem.exists(path)) return false
        return (fileSystem.metadata(path).size ?: 0L) > 0L
    }

    fun segmentSize(path: Path): Long =
        fileSystem.metadata(path).size ?: 0L

    fun writeSink(path: Path): BufferedSink =
        fileSystem.sink(path).buffer()

    fun move(source: Path, target: Path) {
        fileSystem.atomicMove(source, target)
    }

    fun deleteFile(path: Path) {
        runCatching { fileSystem.delete(path) }
            .onFailure { Logger.w(TAG) { "Failed to delete file $path: ${it.message}" } }
    }

    fun mergeSegments(
        segmentsDir: Path,
        totalSegments: Int,
        outputPath: Path,
    ): Long {
        val tempOutputPath = (outputPath.parent
            ?: error("Output path has no parent: $outputPath")) / "${outputPath.name}.part"
        deleteIfExists(tempOutputPath)

        var totalBytesWritten = 0L
        val outputSink = fileSystem.sink(tempOutputPath).buffer()

        try {
            for (index in 0 until totalSegments) {
                val segmentPath = getSegmentPath(segmentsDir, index)
                totalBytesWritten += appendFileToSink(segmentPath, outputSink)
            }
        } catch (e: Exception) {
            deleteIfExists(tempOutputPath)
            throw e
        } finally {
            outputSink.close()
        }

        deleteIfExists(outputPath)
        move(tempOutputPath, outputPath)
        return totalBytesWritten
    }

    fun cleanupSegmentsDir(segmentsDir: Path, totalSegments: Int) {
        for (i in 0 until totalSegments) {
            deleteIfExists(getSegmentPath(segmentsDir, i))
            deleteIfExists(segmentsDir / "segment_$i.ts.part")
        }
        deleteIfExists(segmentsDir)
    }

    fun cleanupTempSegments(download: DownloadEntity) {
        val segmentsDir = buildSegmentsDir(download)
        if (!fileSystem.exists(segmentsDir)) return

        runCatching {
            fileSystem.list(segmentsDir).forEach(::deleteIfExists)
            fileSystem.delete(segmentsDir)
        }.onFailure {
            Logger.w(TAG) { "Failed to cleanup temp segments for $segmentsDir: ${it.message}" }
        }
    }

    fun getEpisodeDir(download: DownloadEntity): Path {
        val rootDir = downloadDirectoryProvider.getDownloadDirectory().toPath()
        val dir = rootDir / "${download.releaseId}" / "${download.episodeOrdinal}_${download.quality}"
        ensureDirectory(dir)
        return dir
    }

    fun moveSegmentsToEpisodeDir(
        segmentsDir: Path,
        totalSegments: Int,
        episodeDir: Path,
    ) {
        for (index in 0 until totalSegments) {
            val src = getSegmentPath(segmentsDir, index)
            val dst = episodeDir / "segment_$index.ts"
            if (fileSystem.exists(src)) {
                move(src, dst)
            }
        }
    }

    fun generateLocalPlaylist(
        episodeDir: Path,
        totalSegments: Int,
        segmentDurations: List<Double> = emptyList(),
        targetDuration: Int = 12,
    ): Path {
        val playlistPath = episodeDir / "playlist.m3u8"
        val content = buildString {
            appendLine("#EXTM3U")
            appendLine("#EXT-X-VERSION:3")
            appendLine("#EXT-X-TARGETDURATION:$targetDuration")
            appendLine("#EXT-X-MEDIA-SEQUENCE:0")
            appendLine("#EXT-X-PLAYLIST-TYPE:VOD")
            for (index in 0 until totalSegments) {
                val duration = segmentDurations.getOrElse(index) { targetDuration.toDouble() }
                appendLine("#EXTINF:$duration,")
                appendLine("segment_$index.ts")
            }
            appendLine("#EXT-X-ENDLIST")
        }
        val sink = fileSystem.sink(playlistPath).buffer()
        try {
            sink.writeUtf8(content)
        } finally {
            sink.close()
        }
        return playlistPath
    }

    fun deleteOutputFile(filePath: String) {
        val path = filePath.toPath()

        if (path.name == "playlist.m3u8") {
            val dir = path.parent ?: return
            runCatching {
                if (fileSystem.exists(dir)) {
                    fileSystem.list(dir).forEach(::deleteIfExists)
                    fileSystem.delete(dir)
                }
            }.onFailure { Logger.w(TAG) { "Failed to delete episode dir $dir: ${it.message}" } }
        } else {
            deleteIfExists(path)
        }

        val parent = path.parent?.parent ?: path.parent ?: return
        runCatching {
            if (fileSystem.exists(parent) && fileSystem.list(parent).isEmpty()) {
                fileSystem.delete(parent)
            }
        }
    }

    private fun getReleaseFolder(download: DownloadEntity): Path {
        val rootDir = downloadDirectoryProvider.getDownloadDirectory().toPath()
        return rootDir / "${download.releaseId}_${sanitize(download.releaseTitle)}"
    }

    private fun buildSegmentsDir(download: DownloadEntity): Path {
        val tempDir = downloadDirectoryProvider.getTempDirectory().toPath()
        return tempDir / "${download.releaseId}_${download.episodeId}_${download.quality}_segments"
    }

    private fun ensureDirectory(path: Path) {
        fileSystem.createDirectories(path)
    }

    fun deleteIfExists(path: Path) {
        runCatching {
            if (fileSystem.exists(path)) {
                fileSystem.delete(path)
            }
        }.onFailure {
            Logger.w(TAG) { "Failed to delete $path: ${it.message}" }
        }
    }

    private fun appendFileToSink(path: Path, outputSink: BufferedSink): Long {
        val source = fileSystem.source(path).buffer()
        var totalBytesRead = 0L

        try {
            var bytesRead: Long
            while (source.read(outputSink.buffer, BUFFER_SIZE.toLong())
                    .also { bytesRead = it } != -1L
            ) {
                totalBytesRead += bytesRead
            }
            outputSink.emitCompleteSegments()
        } finally {
            source.close()
        }

        return totalBytesRead
    }

    companion object {
        private const val TAG = "DownloadFileManager"
        private const val BUFFER_SIZE = 8192
        private val windowsAbsolutePathRegex = Regex("^[A-Za-z]:[/\\\\].*")

        fun sanitize(name: String): String {
            val cleaned = name
                .replace(Regex("[\\\\/:*?\"<>|]"), "_")
                .trim()
                .take(50)

            return cleaned.ifBlank { "unknown" }
        }

        fun formatSize(bytes: Long): String {
            val kb = bytes / 1024.0
            val mb = kb / 1024.0
            val gb = mb / 1024.0

            return when {
                bytes < 1024 -> "$bytes B"
                kb < 1024 -> "${(kb * 10).toLong() / 10.0} KB"
                mb < 1024 -> "${(mb * 100).toLong() / 100.0} MB"
                else -> "${(gb * 100).toLong() / 100.0} GB"
            }
        }

        private fun isAbsolutePath(path: String): Boolean {
            val normalized = normalizePath(path)
            return normalized.startsWith("/") ||
                    normalized.startsWith("file:///") ||
                    windowsAbsolutePathRegex.matches(normalized)
        }

        private fun normalizePath(path: String): String = path.replace('\\', '/')
    }
}
