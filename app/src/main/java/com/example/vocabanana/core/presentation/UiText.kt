package com.example.vocabanana.core.presentation

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource

data class UiText(
    val resId: Int,
    val args: List<Any> = emptyList()
)

@Composable
fun UiText.asString(): String {
    return stringResource(resId, *args.toTypedArray())
}
