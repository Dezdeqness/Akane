package com.dezdeqness.auth.ui.forgot

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.dezdeqness.auth.ui.composables.AuthFormScaffold
import com.dezdeqness.auth.ui.composables.AuthLinkText
import com.dezdeqness.auth.ui.composables.AuthTextField
import com.dezdeqness.core.ui.theme.AppTheme
import com.dezdeqness.core.ui.views.buttons.AppPrimaryButton
import com.dezdeqness.foundation.utils.collectAsStateOnLifecycle
import kotlinx.coroutines.flow.StateFlow

@Composable
fun ForgotPasswordPage(
    stateFlow: StateFlow<ForgotPasswordState>,
    actions: ForgotPasswordActions,
    onHaveTokenClicked: () -> Unit,
    onBackToLoginClicked: () -> Unit,
    onCloseClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val state by stateFlow.collectAsStateOnLifecycle()

    AuthFormScaffold(modifier = modifier, onClose = onCloseClicked) {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                text = "Восстановление пароля",
                color = AppTheme.colors.textPrimary,
                style = AppTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = "Укажите email",
                color = AppTheme.colors.textSecondary,
                style = AppTheme.typography.bodyMedium,
            )
        }

        AuthTextField(
            value = state.email,
            onValueChange = actions::onEmailChanged,
            label = "Email",
            placeholder = "your@email.com",
            keyboardType = KeyboardType.Email,
            imeAction = ImeAction.Done,
            isError = state.errorMessage != null,
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

        AppPrimaryButton(
            title = if (state.isLoading) "Отправка..." else "Восстановить пароль",
            onClick = actions::onSubmitClicked,
            modifier = Modifier.fillMaxWidth(),
        )

        AuthLinkText(
            text = "У меня уже есть токен",
            onClick = onHaveTokenClicked,
            emphasized = true,
        )

        AuthLinkText(
            text = "Вернуться ко входу",
            onClick = onBackToLoginClicked,
        )
    }
}
