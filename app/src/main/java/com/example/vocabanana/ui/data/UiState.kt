package com.example.vocabanana.ui.data

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.vocabanana.R
import com.example.vocabanana.core.presentation.UiText
import com.example.vocabanana.core.presentation.asString


sealed interface UiState<out T> {
    data object Loading : UiState<Nothing>
    data class Success<T>(val data: T) : UiState<T>
    data class Error(val error: UiStateError) : UiState<Nothing>
}

sealed class UiStateError {
    object Unknown : UiStateError()
}

fun UiStateError.toUiText(): UiText = when (this) {
    is UiStateError.Unknown -> UiText(resId = R.string.error_unknown)
}

@Composable
fun <T> ObserveState(state: UiState<T>, content: @Composable (T) -> Unit) {
    when (state) {
        is UiState.Success -> content(state.data)
        is UiState.Error -> {
            ErrorContent(errorText = state.error.toUiText())
        }

        is UiState.Loading -> {
            CircularProgressIndicator()
        }
    }
}

@Composable
fun ErrorContent(modifier: Modifier = Modifier, errorText: UiText) {
    Box(modifier = modifier.fillMaxSize()) {
        Text(errorText.asString())
    }
}