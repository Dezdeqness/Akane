package com.dezdeqness.auth.ui.login

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun LoginStandalonePage(
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel = koinViewModel(),
) {
    val actions = remember(viewModel) {
        object : LoginActions {
            override fun onLoginChanged(value: String) = viewModel.onLoginChanged(value)
            override fun onPasswordChanged(value: String) = viewModel.onPasswordChanged(value)
            override fun onPasswordVisibilityToggled() = viewModel.onPasswordVisibilityToggled()
            override fun onSignInClicked() = viewModel.onSignInClicked()
        }
    }

    LoginPage(
        modifier = modifier,
        stateFlow = viewModel.state,
        actions = actions,
    )
}
