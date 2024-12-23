package com.dezdeqness.network.type

import com.dezdeqness.network.models.core.GeneralResponse
import de.jensklingenberg.ktorfit.Response

typealias ApiResponse<T> = Response<GeneralResponse<T>>
