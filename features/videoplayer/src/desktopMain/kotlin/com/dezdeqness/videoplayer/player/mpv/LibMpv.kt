package com.dezdeqness.videoplayer.player.mpv

import com.sun.jna.Callback
import com.sun.jna.Library
import com.sun.jna.Pointer
import com.sun.jna.StringArray
import com.sun.jna.ptr.PointerByReference

/**
 * Minimal JNA binding for the subset of the libmpv C API we use.
 *
 * We drive mpv through its "render API" in software (`sw`) mode: mpv decodes and,
 * on demand, renders a frame into a raw pixel buffer we own. That buffer is copied
 * into a Skia bitmap and pushed into Compose — same compositing model the VLC
 * callback surface used, so overlays/controls keep drawing on top.
 *
 * Audio is handled entirely by mpv's own audio output, so no manual A/V sync here.
 */
internal interface LibMpv : Library {

    fun mpv_create(): Pointer?
    fun mpv_initialize(handle: Pointer): Int
    fun mpv_terminate_destroy(handle: Pointer)

    fun mpv_set_option_string(handle: Pointer, name: String, data: String): Int
    fun mpv_set_property_string(handle: Pointer, name: String, data: String): Int
    fun mpv_get_property_string(handle: Pointer, name: String): Pointer?

    /** args must be a NULL-terminated array of C strings — [StringArray] handles that. */
    fun mpv_command(handle: Pointer, args: StringArray): Int

    fun mpv_observe_property(handle: Pointer, replyUserdata: Long, name: String, format: Int): Int

    fun mpv_request_log_messages(handle: Pointer, minLevel: String): Int

    /** Returns a pointer to a static mpv_event valid until the next call on this thread. */
    fun mpv_wait_event(handle: Pointer, timeout: Double): Pointer

    fun mpv_free(data: Pointer)

    fun mpv_render_context_create(res: PointerByReference, mpv: Pointer, params: Pointer): Int
    fun mpv_render_context_render(ctx: Pointer, params: Pointer): Int
    fun mpv_render_context_set_update_callback(ctx: Pointer, callback: MpvRenderUpdateFn, data: Pointer?)
    fun mpv_render_context_update(ctx: Pointer): Long
    fun mpv_render_context_free(ctx: Pointer)

    /** typedef void (*mpv_render_update_fn)(void *cb_ctx) */
    fun interface MpvRenderUpdateFn : Callback {
        fun invoke(cbCtx: Pointer?)
    }

    companion object {
        // mpv_format
        const val MPV_FORMAT_NONE = 0
        const val MPV_FORMAT_FLAG = 3
        const val MPV_FORMAT_INT64 = 4
        const val MPV_FORMAT_DOUBLE = 5

        // mpv_event_id
        const val MPV_EVENT_NONE = 0
        const val MPV_EVENT_SHUTDOWN = 1
        const val MPV_EVENT_LOG_MESSAGE = 2
        const val MPV_EVENT_END_FILE = 7
        const val MPV_EVENT_FILE_LOADED = 8
        const val MPV_EVENT_PLAYBACK_RESTART = 21
        const val MPV_EVENT_PROPERTY_CHANGE = 22

        // mpv_end_file_reason (mpv_event_end_file.reason)
        const val MPV_END_FILE_REASON_EOF = 0
        const val MPV_END_FILE_REASON_STOP = 2
        const val MPV_END_FILE_REASON_QUIT = 3
        const val MPV_END_FILE_REASON_ERROR = 4

        // mpv_event_end_file { int reason; int error; ... }
        const val END_FILE_REASON_OFFSET = 0L
        const val END_FILE_ERROR_OFFSET = 4L

        // mpv_event_log_message { const char* prefix; const char* level; const char* text; ... }
        const val LOG_TEXT_OFFSET = 16L

        // mpv_render_param_type (values must match render.h exactly)
        const val MPV_RENDER_PARAM_INVALID = 0
        const val MPV_RENDER_PARAM_API_TYPE = 1
        const val MPV_RENDER_PARAM_SW_SIZE = 17
        const val MPV_RENDER_PARAM_SW_FORMAT = 18
        const val MPV_RENDER_PARAM_SW_STRIDE = 19
        const val MPV_RENDER_PARAM_SW_POINTER = 20

        // mpv_render_context_update() return bitmask
        const val MPV_RENDER_UPDATE_FRAME = 1L

        const val MPV_RENDER_API_TYPE_SW = "sw"

        // struct field offsets on a 64-bit platform (int is 4 bytes, pointer/uint64 are 8)
        // mpv_event { int event_id; int error; uint64 reply_userdata; void* data; }
        const val EVENT_ID_OFFSET = 0L
        const val EVENT_REPLY_USERDATA_OFFSET = 8L
        const val EVENT_DATA_OFFSET = 16L

        // mpv_event_property { const char* name; mpv_format format; void* data; }
        const val PROP_FORMAT_OFFSET = 8L
        const val PROP_DATA_OFFSET = 16L
    }
}
