package com.dezdeqness.profile.data.datasource.impl

import com.dezdeqness.network.services.ProfileService
import com.dezdeqness.profile.contract.model.ProfileEntity
import com.dezdeqness.profile.data.datasource.ProfileApiDatasource
import com.dezdeqness.profile.data.mapper.ProfileMapper

class ProfileApiDatasourceImpl(
    private val profileService: ProfileService,
    private val profileMapper: ProfileMapper,
) : ProfileApiDatasource {

    override suspend fun getProfile(): Result<ProfileEntity> = try {
        val response = profileService.getProfile()
        if (response.isSuccessful) {
            val body = response.body()
            if (body != null) {
                Result.success(profileMapper.mapProfile(body))
            } else {
                Result.failure(Throwable("Empty profile body"))
            }
        } else {
            Result.failure(Throwable("Code: ${response.code}\nError: ${response.errorBody()}"))
        }
    } catch (throwable: Throwable) {
        Result.failure(throwable)
    }
}
