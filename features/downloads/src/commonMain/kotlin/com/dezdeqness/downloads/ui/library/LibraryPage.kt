package com.dezdeqness.downloads.ui.library

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.dezdeqness.core.ui.theme.AppTheme
import com.dezdeqness.core.ui.views.buttons.AppIconButton
import com.dezdeqness.core.ui.views.toolbar.AppToolbar
import com.dezdeqness.designsystem.icons.AkaneIcons
import com.dezdeqness.foundation.utils.collectAsStateOnLifecycle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryPage(
    onReleaseClicked: (releaseId: Long) -> Unit,
    activeDownloadsCountFlow: Flow<Int> = emptyFlow(),
    onActiveDownloadsClicked: () -> Unit = {},
    viewModel: LibraryViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateOnLifecycle()
    val activeDownloadsCount by activeDownloadsCountFlow.collectAsState(initial = 0)

    Scaffold(
        topBar = {
            AppToolbar(
                title = { Text("Библиотека") },
                actions = {
                    AppIconButton(onClick = onActiveDownloadsClicked, contentColor = AppTheme.colors.background) {
                        if (activeDownloadsCount > 0) {
                            BadgedBox(
                                badge = {
                                    Badge { Text(activeDownloadsCount.toString()) }
                                }
                            ) {
                                Icon(
                                    imageVector = AkaneIcons.Download,
                                    contentDescription = null,
                                )
                            }
                        } else {
                            Icon(
                                imageVector = AkaneIcons.Download,
                                contentDescription = null,
                            )
                        }
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
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            if (state.library.isEmpty()) {
                LibraryEmptyState(
                    modifier = Modifier.align(Alignment.Center),
                )
            } else {
                LibraryList(
                    library = state.library,
                    onReleaseClicked = onReleaseClicked,
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
    }
}
