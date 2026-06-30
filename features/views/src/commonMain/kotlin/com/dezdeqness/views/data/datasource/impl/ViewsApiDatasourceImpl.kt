package com.dezdeqness.views.data.datasource.impl

import com.dezdeqness.network.datasource.BaseDataSource
import com.dezdeqness.network.error.ErrorMapper
import com.dezdeqness.network.exception.createApiException
import com.dezdeqness.network.services.ViewsService
import com.dezdeqness.views.contract.model.EpisodeTimecodeEntity
import com.dezdeqness.views.data.datasource.ViewsApiDatasource
import com.dezdeqness.views.data.mapper.TimecodeMapper

class ViewsApiDatasourceImpl(
    private val viewsService: ViewsService,
    private val timecodeMapper: TimecodeMapper,
    errorMapper: ErrorMapper,
) : BaseDataSource(errorMapper), ViewsApiDatasource {

    override suspend fun getReleaseTimecodes(releaseId: Long) = tryWithCatchSuspend {
        val response = viewsService.getReleaseTimecodes(id = releaseId)

        if (response.isSuccessful) {
            val items = response.body().orEmpty().mapNotNull(timecodeMapper::map)
            Result.success(items)
        } else {
            throw response.createApiException()
        }
    }

    override suspend fun saveTimecodes(timecodes: List<EpisodeTimecodeEntity>) = tryWithCatchSuspend {
        val response = viewsService.saveTimecodes(timecodes.map(timecodeMapper::map))

        if (response.isSuccessful) {
            Result.success(Unit)
        } else {
            throw response.createApiException()
        }
    }
}
