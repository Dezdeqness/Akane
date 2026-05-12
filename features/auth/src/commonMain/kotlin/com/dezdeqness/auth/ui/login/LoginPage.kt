package com.dezdeqness.auth.ui.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.dezdeqness.auth.ui.composables.AuthBrandPanel
import com.dezdeqness.auth.ui.login.composables.LoginFormSection
import com.dezdeqness.auth.ui.login.composables.MobileBrand
import com.dezdeqness.core.ui.theme.AppTheme
import com.dezdeqness.designsystem.layouts.AdaptiveLayout
import com.dezdeqness.designsystem.layouts.LayoutType
import com.dezdeqness.designsystem.layouts.LocalLayoutType
import com.dezdeqness.foundation.utils.collectAsStateOnLifecycle
import kotlinx.coroutines.flow.StateFlow


@Composable
fun LoginPage(
    modifier: Modifier = Modifier,
    stateFlow: StateFlow<LoginState>,
    actions: LoginActions,
) {
    val state by stateFlow.collectAsStateOnLifecycle()

    Surface(
        modifier = modifier.fillMaxSize(),
        color = AppTheme.colors.background,
    ) {
        AdaptiveLayout {
            when (LocalLayoutType.current) {
                LayoutType.Mobile -> LoginPageMobile(state = state, actions = actions)
                LayoutType.Tablet -> LoginPageWide(
                    state = state,
                    actions = actions,
                    showStats = false,
                    formMaxWidth = 460.dp,
                )

                LayoutType.Desktop -> LoginPageWide(
                    state = state,
                    actions = actions,
                    showStats = true,
                    formMaxWidth = 520.dp,
                )
            }
        }
    }
}

@Composable
private fun LoginPageMobile(
    state: LoginState,
    actions: LoginActions,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        MobileBrand()
        LoginFormSection(
            state = state,
            actions = actions,
            showHeading = false,
        )
    }
}

@Composable
private fun LoginPageWide(
    state: LoginState,
    actions: LoginActions,
    showStats: Boolean,
    formMaxWidth: Dp,
) {
    Row(modifier = Modifier.fillMaxSize()) {
        AuthBrandPanel(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            showStats = showStats,
        )
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .background(AppTheme.colors.background),
            contentAlignment = Alignment.Center,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .widthIn(max = formMaxWidth)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 32.dp, vertical = 40.dp),
            ) {
                LoginFormSection(
                    state = state,
                    actions = actions,
                    showHeading = true,
                )
            }
        }
    }
}
