package com.dezdeqness.auth.ui.forgot

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.dezdeqness.foundation.utils.collectAsStateOnLifecycle
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ForgotPasswordStandalonePage(
    onEmailSent: () -> Unit,
    onHaveTokenClicked: () -> Unit,
    onBackToLoginClicked: () -> Unit,
    onCloseClicked: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ForgotPasswordViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateOnLifecycle()

    LaunchedEffect(state.isSuccess) {
        if (state.isSuccess) {
            onEmailSent()
        }
    }

    val actions = remember(viewModel) {
        object : ForgotPasswordActions {
            override fun onEmailChanged(value: String) = viewModel.onEmailChanged(value)
            override fun onSubmitClicked() = viewModel.onSubmitClicked()
        }
    }

    ForgotPasswordPage(
        modifier = modifier,
        stateFlow = viewModel.state,
        actions = actions,
        onHaveTokenClicked = onHaveTokenClicked,
        onBackToLoginClicked = onBackToLoginClicked,
        onCloseClicked = onCloseClicked,
    )
}
