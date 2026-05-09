package com.dezdeqness.downloads.contract.model

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
