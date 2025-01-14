package com.dezdeqness.feed.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.ImageLoader
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import coil3.util.DebugLogger
import org.koin.compose.viewmodel.koinViewModel

private const val PAGINATION_LOAD_FACTOR = 0.75

@Composable
fun FeedPage(
    modifier: Modifier = Modifier,
    viewModel: FeedViewModel = koinViewModel(),
    onReleaseClicked: (Long) -> Unit,
) {
    var isPageLoading by remember {
        mutableStateOf(false)
    }

    val context = LocalPlatformContext.current
    val loader = remember {
        ImageLoader.Builder(context)
            .logger(DebugLogger())
            .build()
    }

    LaunchedEffect(Unit) {
        viewModel.onInitialLoad()
    }

    val state by viewModel.feedStateFlow.collectAsStateWithLifecycle()

    LaunchedEffect(state.items) {
        isPageLoading = false
    }

    val hasNextPage = state.hasNextPage


    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.primaryContainer,
    ) {
        if (state.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator()
            }
        } else {
            val lazyListState = rememberLazyListState()

            val shouldStartPaginate = remember {
                derivedStateOf {
                    hasNextPage && (lazyListState.layoutInfo.visibleItemsInfo.lastOrNull()?.index
                        ?: -1) >= (lazyListState.layoutInfo.totalItemsCount * PAGINATION_LOAD_FACTOR)
                }
            }

            LaunchedEffect(isPageLoading, shouldStartPaginate.value) {
                if (shouldStartPaginate.value && isPageLoading.not()) {
                    viewModel.onLoadMore()
                    isPageLoading = true
                }
            }

            LazyColumn(
                state = lazyListState,
                modifier = Modifier.fillMaxSize(),
            ) {
                items(
                    count = state.items.size,
                    key = { index ->
                        state.items[index].id
                    },
                ) { index ->
                    val item = state.items[index]

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(
                                onClick = {
                                    onReleaseClicked.invoke(item.id)
                                }
                            )
                            .padding(horizontal = 16.dp)
                    ) {
                        Row(modifier = Modifier.weight(1f)) {
                            AsyncImage(
                                model = ImageRequest.Builder(context)
                                    .data(item.imageUrl)
                                    .build(),
                                contentDescription = null,
                                imageLoader = loader,
                                modifier = Modifier
                                    .size(150.dp)
                            )
                        }
                        Column(
                            modifier = Modifier
                                .weight(3f)
                                .padding(start = 8.dp, top = 8.dp, bottom = 8.dp)
                        ) {
                            Text(
                                item.title,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                            )

                            Text(
                                item.summary,
                                fontSize = 14.sp,
                                maxLines = 3,
                                overflow = TextOverflow.Ellipsis,
                            )

                        }
                    }
                }

                if (hasNextPage) {
                    item {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .padding(start = 8.dp, end = 4.dp)
                                .padding(vertical = 8.dp),
                        )
                    }
                }
            }
        }

    }
}
