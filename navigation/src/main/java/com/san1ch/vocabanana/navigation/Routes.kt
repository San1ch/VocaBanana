package com.san1ch.vocabanana.navigation

import kotlinx.serialization.Serializable

interface Route

@Serializable
data object InitRoute : Route

@Serializable
data object MainRoute : Route

@Serializable
data object MainSettingsRoute : Route

@Serializable
data object VocabularyRoute : Route

@Serializable
data object TextListRoute : Route

@Serializable
data object AddTextRoute : Route

@Serializable
data object DebugRoute : Route

@Serializable
data object NewWordListRoute : Route
