package com.san1ch.vocabanana.core.ui.model

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.san1ch.vocabanana.core.essentials.model.word.WordState
import com.san1ch.vocabanana.core.ui.R

@SuppressLint("LocalContextGetResourceValueCall")
@Composable
fun WordState.toText(): String {
    val context = LocalContext.current
    return context.getString(
        when (this) {
            WordState.NEW -> R.string.new_word
            WordState.LEARNING -> R.string.learning_word
            WordState.NOT_KNOWN -> R.string.not_known_word
            WordState.KNOWN -> R.string.known_word
            WordState.IGNORED -> R.string.ignored_word
        }
    )
}