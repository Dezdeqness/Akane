package com.dezdeqness.personal.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.dezdeqness.personal.ui.PersonalPage

const val PERSONAL_ROUTE = "personal_route"

fun NavGraphBuilder.personalScreen() {
    composable(PERSONAL_ROUTE) {
        PersonalPage()
    }
}
