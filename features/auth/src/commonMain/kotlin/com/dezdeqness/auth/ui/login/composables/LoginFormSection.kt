package com.dezdeqness.auth.ui.login.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.dezdeqness.auth.ui.login.LoginActions
import com.dezdeqness.auth.ui.login.LoginForm
import com.dezdeqness.auth.ui.login.LoginState
import com.dezdeqness.core.ui.theme.AppTheme

private const val REGISTER_URL = "https://anilibria.top/app/auth/registration/newRegistration"

@Composable
fun LoginFormSection(
    state: LoginState,
    actions: LoginActions,
    showHeading: Boolean,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        if (showHeading) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "Добро пожаловать",
                    color = AppTheme.colors.textPrimary,
                    style = AppTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = "Войдите в свой аккаунт, чтобы продолжить",
                    color = AppTheme.colors.textSecondary,
                    style = AppTheme.typography.bodyMedium,
                )
            }
        }

        LoginForm(
            state = state,
            onLoginChanged = actions::onLoginChanged,
            onPasswordChanged = actions::onPasswordChanged,
            onPasswordVisibilityToggled = actions::onPasswordVisibilityToggled,
            onSignInClicked = actions::onSignInClicked,
        )

        state.errorMessage?.let { message ->
            Text(
                text = message,
                color = AppTheme.colors.error,
                style = AppTheme.typography.bodySmall,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp),
            )
        }

        RegisterPrompt()
    }
}

@Composable
private fun RegisterPrompt() {
    val uriHandler = LocalUriHandler.current
    val link = LinkAnnotation.Url(
        url = REGISTER_URL,
        styles = TextLinkStyles(
            style = SpanStyle(
                color = AppTheme.colors.primary,
                fontWeight = FontWeight.SemiBold,
            ),
        ),
        linkInteractionListener = { uriHandler.openUri(REGISTER_URL) },
    )
    val annotated = buildAnnotatedString {
        withStyle(
            style = SpanStyle(color = AppTheme.colors.textSecondary),
        ) {
            append("Нет аккаунта? ")
        }
        withLink(link) {
            append("Зарегистрироваться")
        }
    }
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = annotated,
            style = AppTheme.typography.bodyMedium,
        )
    }
}
