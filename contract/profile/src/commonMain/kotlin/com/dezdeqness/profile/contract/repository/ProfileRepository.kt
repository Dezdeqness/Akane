package com.dezdeqness.profile.contract.repository

import com.dezdeqness.profile.contract.model.ProfileEntity

interface ProfileRepository {

    suspend fun getProfile(): Result<ProfileEntity>
}
