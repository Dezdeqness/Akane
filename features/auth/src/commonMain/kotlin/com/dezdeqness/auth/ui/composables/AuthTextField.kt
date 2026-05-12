package com.dezdeqness.auth.ui.composables

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import com.dezdeqness.core.ui.theme.AppTheme

@Composable
fun AuthTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Next,
    isPassword: Boolean = false,
    isPasswordVisible: Boolean = false,
    trailingIcon: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        label = { Text(label) },
        placeholder = placeholder?.let { { Text(it) } },
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        visualTransformation = when {
            isPassword && !isPasswordVisible -> PasswordVisualTransformation()
            else -> VisualTransformation.None
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = if (isPassword) KeyboardType.Password else keyboardType,
            imeAction = imeAction,
        ),
        trailingIcon = trailingIcon,
        isError = isError,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = AppTheme.colors.primary,
            unfocusedBorderColor = AppTheme.colors.border,
            focusedLabelColor = AppTheme.colors.primary,
            unfocusedLabelColor = AppTheme.colors.textSecondary,
            cursorColor = AppTheme.colors.primary,
            focusedTextColor = AppTheme.colors.textPrimary,
            unfocusedTextColor = AppTheme.colors.textPrimary,
            focusedContainerColor = AppTheme.colors.background,
            unfocusedContainerColor = AppTheme.colors.background,
            errorBorderColor = AppTheme.colors.error,
            errorLabelColor = AppTheme.colors.error,
        ),
    )
}
