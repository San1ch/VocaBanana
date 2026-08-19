@file:OptIn(ExperimentalSerializationApi::class)

package com.san1ch.vocabanana.core.android.network.googledrive.convertor

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonNamingStrategy

internal fun createGoogleDriveJson(
    isDebug: Boolean
): Json {
    return Json{
        if(isDebug){
            prettyPrint = true
        }
        explicitNulls = false
        encodeDefaults = true
        ignoreUnknownKeys = true
        namingStrategy = JsonNamingStrategy.SnakeCase

    }
}
