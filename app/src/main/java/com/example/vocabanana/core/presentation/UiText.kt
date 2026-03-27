package com.example.vocabanana.core.presentation

import androidx.annotation.StringRes

data class UiText(
    val resId: Int,
    val args: List<Any> = emptyList()
)