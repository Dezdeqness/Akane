package com.dezdeqness.videoplayer.core.player

import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.UIKitInteropProperties
import androidx.compose.ui.viewinterop.UIKitView
import platform.AVFoundation.pause
import platform.AVFoundation.play
import platform.AVKit.AVPlayerViewController
import platform.UIKit.NSLayoutConstraint
import platform.UIKit.UIView

@Composable
actual fun VideoPlayerView(modifier: Modifier, playerState: VideoPlayerState) {
    val player = (playerState.getPlayer() as IosVideoPlayer).avPlayer

    val avPlayerViewController = remember { AVPlayerViewController() }

    avPlayerViewController.player = player
    avPlayerViewController.showsPlaybackControls = false
    avPlayerViewController.allowsPictureInPicturePlayback = false

    UIKitView(
        factory = {
            val playerContainer = UIView()

            avPlayerViewController.view.translatesAutoresizingMaskIntoConstraints = false
            playerContainer.addSubview(avPlayerViewController.view)

            NSLayoutConstraint.activateConstraints(
                listOf(
                    avPlayerViewController.view.leadingAnchor.constraintEqualToAnchor(
                        playerContainer.leadingAnchor
                    ),
                    avPlayerViewController.view.trailingAnchor.constraintEqualToAnchor(
                        playerContainer.trailingAnchor
                    ),
                    avPlayerViewController.view.topAnchor.constraintEqualToAnchor(playerContainer.topAnchor),
                    avPlayerViewController.view.bottomAnchor.constraintEqualToAnchor(playerContainer.bottomAnchor)
                )
            )

            playerContainer
        },
        modifier = modifier.fillMaxSize().aspectRatio(16 / 9f),
        update = { _ ->
            player.play()
        },
        onRelease = {
            player.pause()
        },
        properties = UIKitInteropProperties(
            isInteractive = true,
            isNativeAccessibilityEnabled = true
        )
    )
}
