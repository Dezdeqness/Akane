package com.dezdeqness.downloads.ui.episodes

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import com.dezdeqness.core.ui.theme.AppTheme
import com.dezdeqness.core.ui.views.buttons.AppIconButton
import com.dezdeqness.core.ui.views.toolbar.AppToolbar
import com.dezdeqness.designsystem.icons.AkaneIcons
import com.dezdeqness.foundation.utils.collectAsStateOnLifecycle
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReleaseEpisodesPage(
    onPlayClicked: (releaseId: Long, episodeId: String) -> Unit,
    onBackPressed: () -> Unit,
    viewModel: ReleaseEpisodesViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateOnLifecycle()

    Scaffold(
        topBar = {
            AppToolbar(
                title = {
                    Text(
                        text = state.releaseTitle,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                },
                navigation = {
                    AppIconButton(
                        onClick = onBackPressed,
                        contentColor = AppTheme.colors.background
                    ) {
                        Icon(
                            imageVector = AkaneIcons.Back,
                            contentDescription = null,
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AppTheme.colors.background,
                    titleContentColor = AppTheme.colors.textPrimary,
                ),
            )
        },
        containerColor = AppTheme.colors.background,
    ) { padding ->
        EpisodesList(
            modifier =  Modifier
                .fillMaxSize()
                .padding(padding),
            episodes = state.episodes,
            onPlayClicked = onPlayClicked,
            onDeleteClicked = viewModel::onDeleteClicked,
        )
    }
}
