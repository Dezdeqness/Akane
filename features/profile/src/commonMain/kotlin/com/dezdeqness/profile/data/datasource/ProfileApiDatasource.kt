package com.dezdeqness.profile.data.datasource

import com.dezdeqness.profile.contract.model.ProfileEntity

interface ProfileApiDatasource {
    suspend fun getProfile(): Result<ProfileEntity>
}
