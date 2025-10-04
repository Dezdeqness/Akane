package com.dezdeqness.designsystem.imageloader

import androidx.compose.runtime.Composable
import coil3.ImageLoader

@Composable
expect fun getImageLoader(): ImageLoader
