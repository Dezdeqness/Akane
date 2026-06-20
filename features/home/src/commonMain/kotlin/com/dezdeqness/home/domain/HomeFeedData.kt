package com.dezdeqness.home.domain

import com.dezdeqness.calendar.contract.model.ScheduleItemEntity
import com.dezdeqness.catalog.contract.model.ReleaseEntity
import com.dezdeqness.franchise.contract.model.FranchiseEntity
import com.dezdeqness.genre.contract.model.GenreEntity
import com.dezdeqness.promo.contract.model.PromoEntity

data class HomeFirstPartData(
    val promos: List<PromoEntity>,
    val freshUpdates: List<ScheduleItemEntity>,
    val onGoing: List<ReleaseEntity>,
)

data class HomeSecondPartData(
    val franchises: List<FranchiseEntity>,
    val released: List<ReleaseEntity>,
    val bestRated: List<ReleaseEntity>,
    val genres: List<GenreEntity>,
)
