package com.dezdeqness.shared

import androidx.navigation3.runtime.NavKey
import androidx.savedstate.serialization.SavedStateConfiguration
import com.dezdeqness.auth.navigation.LoginRoute
import com.dezdeqness.auth.navigation.RegisterRoute
import com.dezdeqness.details.navigation.DetailsRoute
import com.dezdeqness.downloads.navigation.ActiveDownloadsRoute
import com.dezdeqness.downloads.navigation.DownloadsRoute
import com.dezdeqness.downloads.navigation.ReleaseEpisodesRoute
import com.dezdeqness.feed.navigation.FeedRoute
import com.dezdeqness.home.navigation.HomeRoute
import com.dezdeqness.personal.navigation.PersonalRoute
import com.dezdeqness.profile.navigation.ProfileRoute
import com.dezdeqness.videoplayer.navigation.DownloadedPlaylistRoute
import com.dezdeqness.videoplayer.navigation.VideoPlayerRoute
import kotlinx.serialization.serializer
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

val navKeysSerializersModule = SerializersModule {
    polymorphic(NavKey::class) {
        subclass(RootShellKey::class, serializer<RootShellKey>())
        subclass(HomeRoute::class, serializer<HomeRoute>())
        subclass(FeedRoute::class, serializer<FeedRoute>())
        subclass(PersonalRoute::class, serializer<PersonalRoute>())
        subclass(DownloadsRoute::class, serializer<DownloadsRoute>())
        subclass(ProfileRoute::class, serializer<ProfileRoute>())
        subclass(LoginRoute::class, serializer<LoginRoute>())
        subclass(RegisterRoute::class, serializer<RegisterRoute>())
        subclass(DetailsRoute::class, serializer<DetailsRoute>())
        subclass(VideoPlayerRoute::class, serializer<VideoPlayerRoute>())
        subclass(DownloadedPlaylistRoute::class, serializer<DownloadedPlaylistRoute>())
        subclass(ReleaseEpisodesRoute::class, serializer<ReleaseEpisodesRoute>())
        subclass(ActiveDownloadsRoute::class, serializer<ActiveDownloadsRoute>())
    }
}

fun navSavedStateConfiguration() = SavedStateConfiguration {
    serializersModule = navKeysSerializersModule
}
