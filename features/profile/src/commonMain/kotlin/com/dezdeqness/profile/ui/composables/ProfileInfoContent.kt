package com.dezdeqness.profile.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dezdeqness.core.ui.theme.AppTheme
import com.dezdeqness.core.ui.views.buttons.AppErrorButton
import com.dezdeqness.core.ui.views.image.AppImage
import com.dezdeqness.profile.ui.model.ProfileUiItem

@Composable
fun ProfileInfoContent(
    modifier: Modifier = Modifier,
    profile: ProfileUiItem,
    onLogoutClicked: () -> Unit,
    avatarSize: Dp = 120.dp,
    contentMaxWidth: Dp = Dp.Unspecified,
) {
    val widthModifier = if (contentMaxWidth != Dp.Unspecified) {
        Modifier.widthIn(max = contentMaxWidth)
    } else {
        Modifier.fillMaxWidth()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 32.dp)
            .then(widthModifier),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Avatar(
            url = profile.avatarUrl,
            placeholder = profile.firstLetter,
            size = avatarSize,
        )

        Text(
            text = profile.nickname,
            color = AppTheme.colors.textPrimary,
            style = AppTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
        )

        profile.joinedDate?.let { date ->
            Text(
                text = "На сайте с $date",
                color = AppTheme.colors.textSecondary,
                style = AppTheme.typography.bodyMedium,
            )
        }

        AppErrorButton(
            title = "Выйти",
            onClick = onLogoutClicked,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp)
                .height(48.dp),
        )
    }
}

@Composable
private fun Avatar(url: String?, placeholder: String, size: Dp) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(AppTheme.colors.surfaceVariant),
        contentAlignment = Alignment.Center,
    ) {
        if (!url.isNullOrBlank()) {
            AppImage(
                data = url,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )
        } else {
            Text(
                text = placeholder.ifBlank { "?" },
                color = AppTheme.colors.textSecondary,
                fontWeight = FontWeight.Bold,
                fontSize = (size.value * 0.4f).sp,
            )
        }
    }
}
