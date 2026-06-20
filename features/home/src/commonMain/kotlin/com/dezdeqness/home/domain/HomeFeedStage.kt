package com.dezdeqness.home.domain

sealed interface HomeFeedStage {
    data class FirstPart(val result: Result<HomeFirstPartData>) : HomeFeedStage
    data class SecondPart(val result: Result<HomeSecondPartData>) : HomeFeedStage
}
