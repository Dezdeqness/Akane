package com.dezdeqness.designsystem.state

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.dezdeqness.core.ui.views.state.StateButtonConfig
import com.dezdeqness.core.ui.views.state.StateButtonStyle
import com.dezdeqness.core.ui.views.state.StateView

@Composable
fun EmptyState(
    modifier: Modifier = Modifier,
    title: String,
    message: String? = null,
    image: @Composable () -> Unit,
    buttonTitle: String,
    onClick: () -> Unit,
) {
    StateView(
        modifier = modifier,
        title = title,
        message = message,
        image = image,
        buttons = buildList {
            add(
                StateButtonConfig(
                    title = buttonTitle,
                    style = StateButtonStyle.Text,
                    onClick = onClick,
                )
            )
        },
    )
}

@Composable
fun EmptyState(
    modifier: Modifier = Modifier,
    title: String,
    message: String? = null,
    image: @Composable () -> Unit,
    buttonsContent: @Composable (() -> Unit)? = null,
) {
    StateView(
        modifier = modifier,
        title = title,
        message = message,
        image = image,
        buttonsContent = buttonsContent,
    )
}
