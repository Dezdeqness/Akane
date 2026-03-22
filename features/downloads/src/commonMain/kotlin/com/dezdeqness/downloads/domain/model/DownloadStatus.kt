package com.dezdeqness.downloads.domain.model

enum class DownloadStatus {
    QUEUED,
    DOWNLOADING,
    PAUSED,
    REMUXING,
    COMPLETED,
    FAILED,
    CANCELLED,
    ;

    val isRetryable: Boolean
        get() = this == CANCELLED || this == FAILED
}
