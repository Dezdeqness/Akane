package com.dezdeqness.feed.domain.repository

import com.dezdeqness.feed.domain.model.FeedEntity

interface FeedRepository {

    suspend fun getFeed(page: Int): Result<FeedEntity>

}
