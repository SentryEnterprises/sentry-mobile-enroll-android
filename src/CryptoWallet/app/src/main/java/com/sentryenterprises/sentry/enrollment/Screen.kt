package com.sentryenterprises.sentry.enrollment

import kotlinx.serialization.Serializable

@Serializable
sealed class Screen {
    @Serializable
    data object GetCardState : Screen()

    @Serializable
    data object Settings : Screen()

    @Serializable
    data object VersionInfo : Screen()

    @Serializable
    data object Reset : Screen()

    @Serializable
    data object Enroll : Screen()

    @Serializable
    data object Verify : Screen()
}
