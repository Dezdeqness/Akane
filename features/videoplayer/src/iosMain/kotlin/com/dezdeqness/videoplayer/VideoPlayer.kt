package com.dezdeqness.videoplayer

import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.interop.UIKitView
import androidx.compose.ui.viewinterop.UIKitInteropProperties
import androidx.compose.ui.viewinterop.UIKitView
import com.dezdeqness.videoplayer.core.FullScreenState
import kotlinx.cinterop.CValue
import kotlinx.cinterop.ExperimentalForeignApi
import platform.AVFoundation.AVPlayer
import platform.AVFoundation.AVPlayerLayer
import platform.AVFoundation.pause
import platform.AVFoundation.play
import platform.AVKit.AVPlayerViewController
import platform.CoreGraphics.CGRect
import platform.Foundation.NSURL
import platform.QuartzCore.CATransaction
import platform.QuartzCore.kCATransactionDisableActions
import platform.UIKit.NSLayoutConstraint
import platform.UIKit.UIView


@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun VideoPlayerScreen(videoUrl: String, systemBarsControllerState: FullScreenState) {
    Surface(
        contentColor = Color.Black,
        modifier = Modifier.fillMaxSize()
    ) {
        AvPlayerView(
            url = videoUrl,
            showControls = false,
            autoPlay = true,
            modifier = Modifier.fillMaxSize().aspectRatio(16 / 9f),
        )
    }

    return
//
//    val player = remember { AVPlayer(uRL = NSURL.URLWithString(videoUrl)!!) }
//    val playerLayer = remember { AVPlayerLayer() }
//    val avPlayerViewController = remember { AVPlayerViewController() }
//    avPlayerViewController.player = player
//    avPlayerViewController.showsPlaybackControls = true
//
//    playerLayer.player = player
//    // Use a UIKitView to integrate with your existing UIKit views
//    UIKitView(
//        factory = {
//            // Create a UIView to hold the AVPlayerLayer
//            val playerContainer = UIView()
//            playerContainer.addSubview(avPlayerViewController.view)
//            // Return the playerContainer as the root UIView
//            playerContainer
//        },
//        onResize = { view: UIView, rect: CValue<CGRect> ->
//            CATransaction.begin()
//            CATransaction.setValue(true, kCATransactionDisableActions)
//            view.layer.setFrame(rect)
//            playerLayer.setFrame(rect)
//            avPlayerViewController.view.layer.frame = rect
//            CATransaction.commit()
//        },
//        update = { view ->
//            player.play()
//            avPlayerViewController.player!!.play()
//        },
//        modifier = Modifier.fillMaxSize().aspectRatio(16 / 9f)
//    )
}

@OptIn(ExperimentalForeignApi::class)
@Composable
fun AvPlayerView(
    modifier: Modifier = Modifier,
    url: String,
    autoPlay: Boolean,
    showControls: Boolean
) {
    val validUrl = remember(url) { NSURL.URLWithString(url) }

    val player = remember {
        validUrl?.let { AVPlayer(uRL = it) }
    }

    val avPlayerViewController = remember { AVPlayerViewController() }

    avPlayerViewController.player = player
    avPlayerViewController.showsPlaybackControls = showControls
    avPlayerViewController.allowsPictureInPicturePlayback = showControls

    UIKitView(
        factory = {
            val playerContainer = UIView()

            avPlayerViewController.view.translatesAutoresizingMaskIntoConstraints = false
            playerContainer.addSubview(avPlayerViewController.view)

            NSLayoutConstraint.activateConstraints(
                listOf(
                    avPlayerViewController.view.leadingAnchor.constraintEqualToAnchor(playerContainer.leadingAnchor),
                    avPlayerViewController.view.trailingAnchor.constraintEqualToAnchor(playerContainer.trailingAnchor),
                    avPlayerViewController.view.topAnchor.constraintEqualToAnchor(playerContainer.topAnchor),
                    avPlayerViewController.view.bottomAnchor.constraintEqualToAnchor(playerContainer.bottomAnchor)
                )
            )

            playerContainer
        },
        modifier = modifier,
        update = { _ ->
            if (autoPlay) {
                player?.play()
            } else {
                player?.pause()
            }
        },
        onRelease = {
            player?.pause()
        },
        properties = UIKitInteropProperties(
            isInteractive = true,
            isNativeAccessibilityEnabled = true
        )
    )
}
