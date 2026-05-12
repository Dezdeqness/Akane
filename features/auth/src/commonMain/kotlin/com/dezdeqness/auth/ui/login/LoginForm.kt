package com.dezdeqness.auth.ui.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.dezdeqness.auth.ui.composables.AuthTextField
import com.dezdeqness.core.ui.theme.AppTheme
import com.dezdeqness.core.ui.views.buttons.AppPrimaryButton

@Composable
fun LoginForm(
    state: LoginState,
    onLoginChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit,
    onPasswordVisibilityToggled: () -> Unit,
    onSignInClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        AuthTextField(
            value = state.login,
            onValueChange = onLoginChanged,
            label = "Email или логин",
            placeholder = "your@email.com",
            keyboardType = KeyboardType.Email,
            imeAction = ImeAction.Next,
        )

        AuthTextField(
            value = state.password,
            onValueChange = onPasswordChanged,
            label = "Пароль",
            placeholder = "••••••••",
            isPassword = true,
            isPasswordVisible = state.isPasswordVisible,
            imeAction = ImeAction.Done,
            trailingIcon = {
                IconButton(onClick = onPasswordVisibilityToggled) {
                    Icon(
                        imageVector = if (state.isPasswordVisible) {
                            Icons.Default.VisibilityOff
                        } else {
                            Icons.Default.Visibility
                        },
                        contentDescription = null,
                        tint = AppTheme.colors.textSecondary,
                    )
                }
            },
        )

        AppPrimaryButton(
            title = if (state.isLoading) "Вход..." else "Войти",
            onClick = onSignInClicked,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
        )
    }
}
