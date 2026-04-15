package com.dezdeqness.personal.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.dezdeqness.personal.ui.PersonalStandalonePage

const val PERSONAL_ROUTE = "personal_route"

fun NavGraphBuilder.personalScreen(
    onItemClicked: (Long, String) -> Unit,
    onEmptyListActionClicked: () -> Unit,
) {
    composable(PERSONAL_ROUTE) {
        PersonalStandalonePage(
            onItemClicked = onItemClicked,
            onEmptyListActionClicked = onEmptyListActionClicked,
        )
    }
}
