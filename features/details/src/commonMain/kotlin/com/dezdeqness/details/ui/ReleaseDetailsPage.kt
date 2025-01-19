package com.dezdeqness.details.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsPage(
    modifier: Modifier = Modifier,
    viewModel: ReleaseDetailsViewModel = koinViewModel(),
    onEpisodeClick: (String) -> Unit,
    onBackPressed: () -> Unit,
) {
    val context = LocalPlatformContext.current
    val loader = remember {
        ImageLoader.Builder(context)
            .logger(DebugLogger())
            .build()
    }

    LaunchedEffect(Unit) {
        viewModel.onInitialLoad()
    }

    val listState = rememberLazyListState()

    val isToolbarTransparent by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex <= 0
        }
    }

    val state by viewModel.releaseDetailsStateFlow.collectAsStateWithLifecycle()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        topBar = {
            TopAppBar(
                title = {
                    if (!isToolbarTransparent && state.status == Status.Loaded) {
                        Text(
                            state.details?.title.orEmpty(),
                            color = MaterialTheme.colorScheme.onPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            onBackPressed()
                        }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null,
                        )
                    }
                },
                colors = TopAppBarDefaults
                    .topAppBarColors()
                    .copy(
                        containerColor = if (isToolbarTransparent) {
                            Color.Transparent
                        } else {
                            MaterialTheme.colorScheme.primary
                        }
                    )
            )
        }
    ) {
        when (state.status) {
            Status.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    Text("Error occurs")
                }
            }

            Status.Loading,
            Status.Initial -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            }

            Status.Loaded -> {
                val details = state.details ?: return@Scaffold

                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    item {
                        AsyncImage(
                            model = remember {
                                ImageRequest.Builder(context)
                                    .data(details.imageUrl)
                                    .build()
                            },
                            contentDescription = null,
                            imageLoader = loader,
                            modifier = Modifier
                                .padding(top = 56.dp)
                                .height(250.dp)
                                .aspectRatio(3 / 4f)
                        )
                    }

                    item {
                        Text(
                            details.title,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            textAlign = TextAlign.Center,
                        )
                    }

                    item {
                        LazyRow(
                            contentPadding = PaddingValues(
                                horizontal = 16.dp,
                                vertical = 4.dp
                            )
                        ) {
                            items(details.genres.size) { index ->
                                val item = details.genres[index]
                                SuggestionChip(
                                    enabled = false,
                                    onClick = {},
                                    label = {
                                        Text(
                                            item,
                                            fontSize = 14.sp,
                                            color = MaterialTheme.colorScheme.onPrimary,
                                        )
                                    },
                                    modifier = Modifier.padding(end = 8.dp)
                                )
                            }
                        }
                    }

                    item {
                        OutlinedCard(
                            onClick = {},
                            enabled = false,
                            colors = CardDefaults.outlinedCardColors().copy(
                                disabledContentColor = Color.Transparent,
                                disabledContainerColor = Color.Transparent,
                            ),
                            modifier = Modifier.padding(horizontal = 16.dp).padding(bottom = 40.dp)
                        ) {
                            Text(
                                details.summary,
                                fontSize = 16.sp,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.78f),
                            )
                        }
                    }

                    item {
                        Text(
                            "Вышедшие эпизоды",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.fillMaxWidth()
                                .padding(vertical = 8.dp, horizontal = 16.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            textAlign = TextAlign.Start,
                        )
                    }

                    items(details.episodes.size) { index ->
                        val item = details.episodes[index]

                        Card(
                            modifier = Modifier
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                                .fillMaxWidth()
                                .clickable(
                                    onClick = {
                                        onEpisodeClick.invoke(item.hls720.orEmpty())
                                    }
                                )
                        ) {
                            Box {
                                AsyncImage(
                                    model = remember {
                                        ImageRequest.Builder(context)
                                            .data(item.previewUrl)
                                            .build()
                                    },
                                    contentScale = ContentScale.FillWidth,
                                    contentDescription = null,
                                    imageLoader = loader,
                                    colorFilter = ColorFilter.tint(Color.Gray, blendMode = BlendMode.Darken),
                                    modifier = Modifier
                                        .height(200.dp)
                                        .blur(
                                            radiusX = 10.dp,
                                            radiusY = 10.dp,
                                            edgeTreatment = BlurredEdgeTreatment(
                                                RoundedCornerShape(8.dp)
                                            )
                                        )
                                )

                                Column(modifier = Modifier.align(alignment = Alignment.BottomStart)) {
                                    Text(
                                        item.name,
                                        fontSize = 16.sp,
                                        modifier = Modifier.padding(horizontal = 16.dp),
                                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.78f),
                                    )

                                    Text(
                                        "${item.ordinal} эпизод",
                                        fontSize = 18.sp,
                                        modifier = Modifier.padding(
                                            horizontal = 16.dp,
                                            vertical = 8.dp
                                        ),
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onPrimary
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

    }
}
