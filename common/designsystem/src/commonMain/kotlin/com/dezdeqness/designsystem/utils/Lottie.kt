package com.dezdeqness.designsystem.utils

import akane.common.designsystem.generated.resources.Res

object LottieRes {
    suspend fun readBytes(item: LottieFiles) = Res.readBytes(item.path)
}

enum class LottieFiles(val path: String) {
    LottieEmptyV1("files/empty_v1.lottie"),
    LottieEmptyV2("files/empty_v2.lottie"),
    LottieEmptyV3("files/empty_v3.lottie"),
    LottieErrorV1("files/error_v1.lottie"),
    LottieErrorV2("files/error_v2.lottie"),
}
