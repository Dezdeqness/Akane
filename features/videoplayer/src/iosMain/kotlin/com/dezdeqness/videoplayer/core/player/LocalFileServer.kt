package com.dezdeqness.videoplayer.core.player

import co.touchlab.kermit.Logger
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.alloc
import kotlinx.cinterop.convert
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.reinterpret
import kotlinx.cinterop.sizeOf
import kotlinx.cinterop.usePinned
import kotlinx.cinterop.value
import platform.Foundation.NSData
import platform.Foundation.NSThread
import platform.Foundation.NSURL
import platform.Foundation.dataWithContentsOfURL
import platform.Foundation.subdataWithRange
import platform.Foundation.NSMakeRange
import platform.posix.AF_INET
import platform.posix.SOCK_STREAM
import platform.posix.accept
import platform.posix.bind
import platform.posix.close
import platform.posix.getsockname
import platform.posix.listen
import platform.posix.memcpy
import platform.posix.read
import platform.posix.send
import platform.posix.sockaddr_in
import platform.posix.socket
import platform.posix.socklen_tVar

private const val TAG = "LocalFileServer"
private const val BUFFER_SIZE = 8192

private fun htons16(value: UShort): UShort {
    val v = value.toInt()
    return (((v and 0xFF) shl 8) or ((v shr 8) and 0xFF)).toUShort()
}

private fun ntohs16(value: UShort): UShort = htons16(value)

private fun inAddrLoopback(): UInt = 0x0100007fu

@OptIn(ExperimentalForeignApi::class)
class LocalFileServer {

    private var serverSocket: Int = -1
    private var serverThread: NSThread? = null
    var port: Int = 0
        private set
    private var baseDirectory: String = ""
    private var isRunning = false

    fun start(directory: String): String {
        if (isRunning) {
            return "http://127.0.0.1:$port"
        }
        baseDirectory = directory

        memScoped {
            serverSocket = socket(AF_INET, SOCK_STREAM, 0)
            if (serverSocket < 0) {
                Logger.e(TAG) { "Failed to create socket" }
                return ""
            }

            val addr = alloc<sockaddr_in>()
            addr.sin_family = AF_INET.convert()
            addr.sin_port = htons16(0u)
            addr.sin_addr.s_addr = inAddrLoopback()

            val bindResult = bind(
                serverSocket,
                addr.ptr.reinterpret(),
                sizeOf<sockaddr_in>().convert()
            )
            if (bindResult < 0) {
                Logger.e(TAG) { "Failed to bind" }
                close(serverSocket)
                return ""
            }

            if (listen(serverSocket, 10) < 0) {
                Logger.e(TAG) { "Failed to listen" }
                close(serverSocket)
                return ""
            }

            val boundAddr = alloc<sockaddr_in>()
            val addrLen = alloc<socklen_tVar>()
            addrLen.value = sizeOf<sockaddr_in>().convert()
            getsockname(serverSocket, boundAddr.ptr.reinterpret(), addrLen.ptr)
            port = ntohs16(boundAddr.sin_port).toInt()
        }

        isRunning = true
        Logger.d(TAG) { "Server started on port $port, serving $baseDirectory" }

        serverThread = NSThread {
            acceptLoop()
        }.apply { start() }

        return "http://127.0.0.1:$port"
    }

    fun stop() {
        isRunning = false
        if (serverSocket >= 0) {
            close(serverSocket)
            serverSocket = -1
        }
        serverThread = null
        Logger.d(TAG) { "Server stopped" }
    }

    private fun acceptLoop() {
        while (isRunning) {
            memScoped {
                val clientAddr = alloc<sockaddr_in>()
                val addrLen = alloc<socklen_tVar>()
                addrLen.value = sizeOf<sockaddr_in>().convert()

                val clientSocket = accept(serverSocket, clientAddr.ptr.reinterpret(), addrLen.ptr)
                if (clientSocket < 0) {
                    if (isRunning) Logger.e(TAG) { "Accept failed" }
                    return@memScoped
                }

                handleClient(clientSocket)
            }
        }
    }

    private fun handleClient(clientSocket: Int) {
        try {
            val requestLine = readRequestLine(clientSocket)
            if (requestLine == null) {
                close(clientSocket)
                return
            }

            Logger.d(TAG) { "Request: $requestLine" }

            val parts = requestLine.split(" ")
            if (parts.size < 2 || parts[0] != "GET") {
                sendError(clientSocket, 400, "Bad Request")
                return
            }

            val path = parts[1].removePrefix("/")
            val filePath = "$baseDirectory/$path"

            val fileUrl = NSURL.fileURLWithPath(filePath)
            val data = NSData.dataWithContentsOfURL(fileUrl)
            if (data == null) {
                Logger.e(TAG) { "File not found: $filePath" }
                sendError(clientSocket, 404, "Not Found")
                return
            }

            val contentType = when {
                path.endsWith(".m3u8") -> "application/vnd.apple.mpegurl"
                path.endsWith(".ts") -> "video/mp2t"
                else -> "application/octet-stream"
            }

            val header = "HTTP/1.1 200 OK\r\n" +
                    "Content-Type: $contentType\r\n" +
                    "Content-Length: ${data.length}\r\n" +
                    "Connection: close\r\n" +
                    "Access-Control-Allow-Origin: *\r\n" +
                    "\r\n"

            sendAll(clientSocket, header.encodeToByteArray())
            sendNSData(clientSocket, data)
        } finally {
            close(clientSocket)
        }
    }

    private fun readRequestLine(socket: Int): String? {
        val buffer = ByteArray(4096)
        val bytesRead = buffer.usePinned { pinned ->
            read(socket, pinned.addressOf(0), buffer.size.convert()).toInt()
        }
        if (bytesRead <= 0) return null
        val request = buffer.decodeToString(0, bytesRead)
        return request.lineSequence().firstOrNull()
    }

    private fun sendError(socket: Int, code: Int, message: String) {
        val response = "HTTP/1.1 $code $message\r\nContent-Length: 0\r\nConnection: close\r\n\r\n"
        sendAll(socket, response.encodeToByteArray())
    }

    private fun sendAll(socket: Int, data: ByteArray) {
        var offset = 0
        data.usePinned { pinned ->
            while (offset < data.size) {
                val sent = send(socket, pinned.addressOf(offset), (data.size - offset).convert(), 0).toInt()
                if (sent <= 0) break
                offset += sent
            }
        }
    }

    private fun sendNSData(socket: Int, data: NSData) {
        val total = data.length.toLong()
        if (total == 0L) return
        val chunkSize = BUFFER_SIZE
        val buffer = ByteArray(chunkSize)
        var offset = 0L

        while (offset < total) {
            val remaining = (total - offset).toInt()
            val toSend = minOf(remaining, chunkSize)
            val subdata = data.subdataWithRange(
                NSMakeRange(offset.toULong(), toSend.toULong())
            )
            val subBytes = subdata.bytes ?: return
            buffer.usePinned { pinned ->
                memcpy(pinned.addressOf(0), subBytes, toSend.convert())
                var sent = 0
                while (sent < toSend) {
                    val result = send(socket, pinned.addressOf(sent), (toSend - sent).convert(), 0).toInt()
                    if (result <= 0) return
                    sent += result
                }
            }
            offset += toSend
        }
    }
}
