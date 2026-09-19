package com.dezdeqness.auth.ui.reset

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.dezdeqness.foundation.utils.collectAsStateOnLifecycle
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ResetPasswordStandalonePage(
    onPasswordReset: () -> Unit,
    onBackToLoginClicked: () -> Unit,
    onCloseClicked: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ResetPasswordViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateOnLifecycle()

    LaunchedEffect(state.isSuccess) {
        if (state.isSuccess) {
            onPasswordReset()
        }
    }

    val actions = remember(viewModel) {
        object : ResetPasswordActions {
            override fun onTokenChanged(value: String) = viewModel.onTokenChanged(value)
            override fun onPasswordChanged(value: String) = viewModel.onPasswordChanged(value)
            override fun onPasswordConfirmationChanged(value: String) =
                viewModel.onPasswordConfirmationChanged(value)

            override fun onPasswordVisibilityToggled() = viewModel.onPasswordVisibilityToggled()
            override fun onConfirmationVisibilityToggled() =
                viewModel.onConfirmationVisibilityToggled()

            override fun onSubmitClicked() = viewModel.onSubmitClicked()
        }
    }

    ResetPasswordPage(
        modifier = modifier,
        stateFlow = viewModel.state,
        actions = actions,
        onBackToLoginClicked = onBackToLoginClicked,
        onCloseClicked = onCloseClicked,
    )
}
