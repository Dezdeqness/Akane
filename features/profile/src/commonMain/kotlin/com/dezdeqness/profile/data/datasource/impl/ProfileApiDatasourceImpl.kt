package com.dezdeqness.profile.data.datasource.impl

import com.dezdeqness.network.datasource.BaseDataSource
import com.dezdeqness.network.error.ErrorMapper
import com.dezdeqness.network.exception.createApiException
import com.dezdeqness.network.services.ProfileService
import com.dezdeqness.profile.contract.model.ProfileEntity
import com.dezdeqness.profile.data.datasource.ProfileApiDatasource
import com.dezdeqness.profile.data.mapper.ProfileMapper

class ProfileApiDatasourceImpl(
    private val profileService: ProfileService,
    private val profileMapper: ProfileMapper,
    errorMapper: ErrorMapper,
) : BaseDataSource(errorMapper), ProfileApiDatasource {

    override suspend fun getProfile(): Result<ProfileEntity> = tryWithCatchSuspend {
        val response = profileService.getProfile()
        if (response.isSuccessful) {
            val body = response.body()
                ?: return@tryWithCatchSuspend Result.failure(Throwable("Empty profile body"))
            Result.success(profileMapper.mapProfile(body))
        } else {
            throw response.createApiException()
        }
    }
}
