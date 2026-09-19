package com.dezdeqness.auth.ui.reset

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
fun ResetPasswordPage(
    stateFlow: StateFlow<ResetPasswordState>,
    actions: ResetPasswordActions,
    onBackToLoginClicked: () -> Unit,
    onCloseClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val state by stateFlow.collectAsStateOnLifecycle()

    AuthFormScaffold(modifier = modifier, onClose = onCloseClicked) {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                text = "Новый пароль",
                color = AppTheme.colors.textPrimary,
                style = AppTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = "Введите токен из письма и новый пароль",
                color = AppTheme.colors.textSecondary,
                style = AppTheme.typography.bodyMedium,
            )
        }

        AuthTextField(
            value = state.token,
            onValueChange = actions::onTokenChanged,
            label = "Токен из письма",
            placeholder = "Вставьте токен",
            imeAction = ImeAction.Next,
            isError = state.errorMessage != null,
        )

        AuthTextField(
            value = state.password,
            onValueChange = actions::onPasswordChanged,
            label = "Новый пароль",
            placeholder = "••••••••",
            isPassword = true,
            isPasswordVisible = state.isPasswordVisible,
            imeAction = ImeAction.Next,
            isError = state.errorMessage != null,
            trailingIcon = {
                PasswordVisibilityIcon(
                    visible = state.isPasswordVisible,
                    onClick = actions::onPasswordVisibilityToggled,
                )
            },
        )

        AuthTextField(
            value = state.passwordConfirmation,
            onValueChange = actions::onPasswordConfirmationChanged,
            label = "Подтверждение пароля",
            placeholder = "••••••••",
            isPassword = true,
            isPasswordVisible = state.isConfirmationVisible,
            imeAction = ImeAction.Done,
            isError = state.errorMessage != null,
            trailingIcon = {
                PasswordVisibilityIcon(
                    visible = state.isConfirmationVisible,
                    onClick = actions::onConfirmationVisibilityToggled,
                )
            },
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
            title = if (state.isLoading) "Сохранение..." else "Сохранить новый пароль",
            onClick = actions::onSubmitClicked,
            modifier = Modifier.fillMaxWidth(),
        )

        AuthLinkText(
            text = "Авторизация",
            onClick = onBackToLoginClicked,
            emphasized = true,
        )
    }
}

@Composable
private fun PasswordVisibilityIcon(
    visible: Boolean,
    onClick: () -> Unit,
) {
    IconButton(onClick = onClick) {
        Icon(
            imageVector = if (visible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
            contentDescription = null,
            tint = AppTheme.colors.textSecondary,
        )
    }
}
