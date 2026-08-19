package com.san1ch.vocabanana.core.android.network.googledrive.clients

import okhttp3.OkHttpClient
import java.time.Duration

fun createGoogleDriveOkHttpClient(
    timeout: Duration
): OkHttpClient {
    return OkHttpClient.Builder().callTimeout(timeout).build()
}