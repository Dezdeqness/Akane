package com.dezdeqness.videoplayer.player.mpv

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import co.touchlab.kermit.Logger
import com.dezdeqness.videoplayer.core.player.PlayerEvent
import com.sun.jna.Memory
import com.sun.jna.Pointer
import com.sun.jna.StringArray
import com.sun.jna.ptr.PointerByReference
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import org.jetbrains.skia.ColorAlphaType
import org.jetbrains.skia.ColorType
import org.jetbrains.skia.Image
import org.jetbrains.skia.ImageInfo
import java.util.concurrent.Semaphore
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean

internal class Mpv {

    private val lib = MpvLibraryLoader.load()
    private val handle: Pointer = run {
        MpvLibraryLoader.ensureCNumericLocale() // libmpv needs LC_NUMERIC=C, AWT breaks it
        lib.mpv_create() ?: error("mpv_create() failed")
    }

    private val _events = MutableSharedFlow<PlayerEvent>(extraBufferCapacity = 64)
    val events: Flow<PlayerEvent> = _events.asSharedFlow()

    private val _videoFrame = MutableStateFlow<ImageBitmap?>(null)
    val videoFrame: StateFlow<ImageBitmap?> = _videoFrame.asStateFlow()

    private val running = AtomicBoolean(true)
    private val frameSignal = Semaphore(0)

    @Volatile private var pendingStartMs = 0L
    @Volatile private var frameWidth = 0
    @Volatile private var frameHeight = 0

    private val eventThread = Thread(::eventLoop, "mpv-events").apply { isDaemon = true }
    private val renderThread = Thread(::renderLoop, "mpv-render").apply { isDaemon = true }

    init {
        lib.mpv_set_option_string(handle, "vo", "libmpv")
        lib.mpv_set_option_string(handle, "hwdec", "no")
        lib.mpv_set_option_string(handle, "idle", "yes")
        lib.mpv_set_option_string(handle, "terminal", "no")
        check(lib.mpv_initialize(handle) >= 0) { "mpv_initialize failed" }
        lib.mpv_request_log_messages(handle, "error")

        createRenderContext()
        ObservedProperty.entries.forEach {
            lib.mpv_observe_property(handle, it.id, it.propName, it.format.raw)
        }

        eventThread.start()
        renderThread.start()
        Logger.i(TAG) { "Mpv engine initialized (sw render, hwdec=no)" }
    }

    fun play() = setProperty("pause", "no")

    fun pause() = setProperty("pause", "yes")

    fun stop() {
        command("stop")
        emit(PlayerEvent.IsPlaying(false))
    }

    fun seekTo(positionMs: Long) {
        val seconds = (positionMs.coerceAtLeast(0) / 1000.0).toString()
        command("seek", seconds, "absolute")
        emit(PlayerEvent.PositionChanged(positionMs))
    }

    fun seekForward() = command("seek", "10", "relative")

    fun seekBack() = command("seek", "-10", "relative")

    fun setSpeed(speed: Float) = setProperty("speed", speed.coerceIn(0.25f, 3f).toString())

    fun setVolume(volume: Float) =
        setProperty("volume", (volume.coerceIn(0f, 1f) * 100).toInt().toString())

    fun load(url: String, startPositionMs: Long) {
        pendingStartMs = startPositionMs
        emit(PlayerEvent.IsBuffering(true))
        Logger.i(TAG) { "loadfile: $url" }
        command("loadfile", url, "replace")
    }

    fun release() {
        if (!running.compareAndSet(true, false)) return
        frameSignal.release()
        runCatching { eventThread.join(THREAD_JOIN_MS) }
        runCatching { renderThread.join(THREAD_JOIN_MS) }
        renderCtx?.let { lib.mpv_render_context_free(it) }
        renderCtx = null
        lib.mpv_terminate_destroy(handle)
    }

    private fun command(vararg args: String) {
        lib.mpv_command(handle, StringArray(args, "UTF-8"))
    }

    private fun setProperty(name: String, value: String) {
        lib.mpv_set_property_string(handle, name, value)
    }

    private fun getString(name: String): String? {
        val ptr = lib.mpv_get_property_string(handle, name) ?: return null
        return try {
            ptr.getString(0)
        } finally {
            lib.mpv_free(ptr)
        }
    }

    private fun emit(event: PlayerEvent) {
        _events.tryEmit(event)
    }

    private fun emitPlayingState() {
        emit(PlayerEvent.IsPlaying(getString("pause") != "yes"))
    }

    private fun eventLoop() {
        while (running.get()) {
            val ev = lib.mpv_wait_event(handle, EVENT_TIMEOUT_SECONDS)
            when (ev.getInt(LibMpv.EVENT_ID_OFFSET)) {
                LibMpv.MPV_EVENT_SHUTDOWN -> running.set(false)
                LibMpv.MPV_EVENT_FILE_LOADED -> {
                    val start = pendingStartMs
                    if (start > 0) {
                        pendingStartMs = 0
                        seekTo(start)
                    }
                    emitPlayingState()
                }
                LibMpv.MPV_EVENT_PLAYBACK_RESTART -> emitPlayingState()
                LibMpv.MPV_EVENT_END_FILE -> handleEndFile(ev)
                LibMpv.MPV_EVENT_LOG_MESSAGE -> {
                    val text = ev.getPointer(LibMpv.EVENT_DATA_OFFSET)
                        ?.getPointer(LibMpv.LOG_TEXT_OFFSET)?.getString(0)?.trimEnd()
                    if (!text.isNullOrEmpty()) Logger.e(TAG) { "mpv: $text" }
                }
                LibMpv.MPV_EVENT_PROPERTY_CHANGE -> handlePropertyChange(ev)
            }
        }
    }

    private fun handleEndFile(ev: Pointer) {
        val data = ev.getPointer(LibMpv.EVENT_DATA_OFFSET)
        when (data?.getInt(LibMpv.END_FILE_REASON_OFFSET) ?: LibMpv.MPV_END_FILE_REASON_EOF) {
            LibMpv.MPV_END_FILE_REASON_EOF -> {
                emit(PlayerEvent.PlaybackEnded)
                emit(PlayerEvent.IsPlaying(false))
            }
            LibMpv.MPV_END_FILE_REASON_ERROR -> {
                Logger.e(TAG) { "playback error (end-file reason=error)" }
                emit(PlayerEvent.Error("mpv playback error"))
                emit(PlayerEvent.IsPlaying(false))
            }
            else -> emit(PlayerEvent.IsPlaying(false))
        }
    }

    private fun handlePropertyChange(ev: Pointer) {
        val id = ev.getLong(LibMpv.EVENT_REPLY_USERDATA_OFFSET)
        val prop = ev.getPointer(LibMpv.EVENT_DATA_OFFSET) ?: return
        val format = prop.getInt(LibMpv.PROP_FORMAT_OFFSET)
        if (format == LibMpv.MPV_FORMAT_NONE) return
        val valuePtr = prop.getPointer(LibMpv.PROP_DATA_OFFSET) ?: return

        when (ObservedProperty.fromId(id)) {
            ObservedProperty.TimePos ->
                emit(PlayerEvent.PositionChanged((valuePtr.getDouble(0) * 1000).toLong()))
            ObservedProperty.Duration ->
                emit(PlayerEvent.DurationChanged((valuePtr.getDouble(0) * 1000).toLong()))
            ObservedProperty.Pause ->
                emit(PlayerEvent.IsPlaying(valuePtr.getInt(0) == 0))
            ObservedProperty.Cache ->
                emit(PlayerEvent.IsBuffering(valuePtr.getInt(0) != 0))
            ObservedProperty.Width -> {
                frameWidth = valuePtr.getLong(0).toInt()
                frameSignal.release()
            }
            ObservedProperty.Height -> {
                frameHeight = valuePtr.getLong(0).toInt()
                frameSignal.release()
            }
            null -> Unit
        }
    }

    private var renderCtx: Pointer? = null

    private var updateCallback: LibMpv.MpvRenderUpdateFn? = null
    private val swFormatMem = cString(SW_PIXEL_FORMAT)

    private var targetBuf: Memory? = null
    private var pixels: ByteArray? = null
    private var renderParams: Memory? = null
    private var paramData: List<Memory> = emptyList()
    private var bufW = 0
    private var bufH = 0
    private var renderErrorLogged = false
    private var firstFrameLogged = false

    private fun createRenderContext() {
        val params = Memory(2 * PARAM_SIZE).apply {
            putParam(0, LibMpv.MPV_RENDER_PARAM_API_TYPE, cString(LibMpv.MPV_RENDER_API_TYPE_SW))
            putParam(1, LibMpv.MPV_RENDER_PARAM_INVALID, null)
        }
        val ref = PointerByReference()
        check(lib.mpv_render_context_create(ref, handle, params) >= 0) {
            "mpv_render_context_create failed"
        }
        val ctx = ref.value
        renderCtx = ctx
        val callback = LibMpv.MpvRenderUpdateFn { frameSignal.release() }
        updateCallback = callback
        lib.mpv_render_context_set_update_callback(ctx, callback, null)
    }

    private fun renderLoop() {
        while (running.get()) {
            frameSignal.tryAcquire(RENDER_POLL_MS, TimeUnit.MILLISECONDS)
            if (!running.get()) break
            val ctx = renderCtx ?: continue
            if (lib.mpv_render_context_update(ctx) and LibMpv.MPV_RENDER_UPDATE_FRAME == 0L) continue

            val w = frameWidth
            val h = frameHeight
            if (w <= 0 || h <= 0) continue

            val bytes = renderFrame(ctx, w, h) ?: continue

            val image = Image.makeRaster(
                imageInfo = ImageInfo(w, h, FRAME_COLOR_TYPE, ColorAlphaType.OPAQUE),
                bytes = bytes,
                rowBytes = w * BYTES_PER_PIXEL,
            )
            _videoFrame.value = image.toComposeImageBitmap()

            if (!firstFrameLogged) {
                firstFrameLogged = true
                Logger.i(TAG) { "First video frame rendered (${w}x$h)" }
            }
        }
    }

    private fun renderFrame(ctx: Pointer, width: Int, height: Int): ByteArray? {
        if (width != bufW || height != bufH) resizeSurface(width, height)
        val params = renderParams ?: return null
        val buf = targetBuf ?: return null
        val px = pixels ?: return null

        val rc = lib.mpv_render_context_render(ctx, params)
        if (rc < 0) {
            if (!renderErrorLogged) {
                renderErrorLogged = true
                Logger.e(TAG) { "mpv_render_context_render failed rc=$rc (${width}x$height)" }
            }
            return null
        }
        buf.read(0, px, 0, px.size)
        return px
    }

    private fun resizeSurface(w: Int, h: Int) {
        bufW = w
        bufH = h
        val stride = w.toLong() * BYTES_PER_PIXEL
        val buf = Memory(stride * h)
        targetBuf = buf
        pixels = ByteArray((stride * h).toInt())

        val sizeMem = Memory(8).apply { setInt(0, w); setInt(4, h) }
        val strideMem = Memory(8).apply { setLong(0, stride) }
        renderParams = Memory(5 * PARAM_SIZE).apply {
            putParam(0, LibMpv.MPV_RENDER_PARAM_SW_SIZE, sizeMem)
            putParam(1, LibMpv.MPV_RENDER_PARAM_SW_FORMAT, swFormatMem)
            putParam(2, LibMpv.MPV_RENDER_PARAM_SW_STRIDE, strideMem)
            putParam(3, LibMpv.MPV_RENDER_PARAM_SW_POINTER, buf)
            putParam(4, LibMpv.MPV_RENDER_PARAM_INVALID, null)
        }
        paramData = listOf(sizeMem, strideMem)
    }

    private fun Memory.putParam(index: Int, type: Int, data: Pointer?) {
        val off = index.toLong() * PARAM_SIZE
        setInt(off, type)
        setPointer(off + POINTER_OFFSET, data)
    }

    private fun cString(s: String): Memory {
        val bytes = s.toByteArray(Charsets.UTF_8)
        return Memory((bytes.size + 1).toLong()).apply {
            write(0, bytes, 0, bytes.size)
            setByte(bytes.size.toLong(), 0)
        }
    }

    private enum class ObservedProperty(val id: Long, val propName: String, val format: MpvFormat) {
        TimePos(1, "time-pos", MpvFormat.Double),
        Duration(2, "duration", MpvFormat.Double),
        Pause(3, "pause", MpvFormat.Flag),
        Cache(4, "paused-for-cache", MpvFormat.Flag),
        Width(5, "dwidth", MpvFormat.Int64),
        Height(6, "dheight", MpvFormat.Int64);

        companion object {
            fun fromId(id: Long): ObservedProperty? = entries.firstOrNull { it.id == id }
        }
    }

    private enum class MpvFormat(val raw: Int) {
        Flag(LibMpv.MPV_FORMAT_FLAG),
        Int64(LibMpv.MPV_FORMAT_INT64),
        Double(LibMpv.MPV_FORMAT_DOUBLE),
    }

    private companion object {
        const val TAG = "Mpv"
        const val EVENT_TIMEOUT_SECONDS = 0.05
        const val RENDER_POLL_MS = 100L
        const val THREAD_JOIN_MS = 1000L

        const val PARAM_SIZE = 16L
        const val POINTER_OFFSET = 8L

        const val SW_PIXEL_FORMAT = "rgb0"
        const val BYTES_PER_PIXEL = 4
        val FRAME_COLOR_TYPE = ColorType.RGB_888X
    }
}
