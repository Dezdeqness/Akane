package com.dezdeqness.profile.data.repository

import com.dezdeqness.profile.contract.repository.ProfileRepository
import com.dezdeqness.profile.data.datasource.ProfileApiDatasource

class ProfileRepositoryImpl(
    private val profileApiDatasource: ProfileApiDatasource,
) : ProfileRepository {

    override suspend fun getProfile() = profileApiDatasource.getProfile()
}
