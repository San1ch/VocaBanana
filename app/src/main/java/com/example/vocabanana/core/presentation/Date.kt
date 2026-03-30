package com.example.vocabanana.core.presentation

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

fun Long.toFormattedDate(pattern: String = "dd.MM.yyyy HH:mm"): String {
    if (this <= 0L) return ""

    val instant = Instant.ofEpochMilli(this)
    val formatter = DateTimeFormatter.ofPattern(pattern, Locale.getDefault())

    return instant.atZone(ZoneId.systemDefault())
        .toLocalDateTime()
        .format(formatter)
}