package com.san1ch.vocabanana.core.android.network.googledrive

import java.time.Duration

data class GoogleDriveConfig (
    val baseUrl: String = "https://www.googleapis.com/",
    val timeout: Duration = Duration.ofSeconds(10),
    val isDebug: Boolean = true,
)