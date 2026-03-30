package com.example.vocabanana.core.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.vocabanana.core.presentation.uistate.UiState
import com.example.vocabanana.core.presentation.uistate.UiStateError
import com.example.vocabanana.core.presentation.uistate.toUiText
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map


@Composable
fun <T : Any> StateObserver(
    state: UiState<T>,
    onLoading: @Composable () -> Unit = { DefaultLoader() },
    onError: @Composable (String) -> Unit = { ErrorContent(errorText = it) },
    onSuccess: @Composable (T) -> Unit
) {
    val context = LocalContext.current
    when (state) {
        is UiState.Loading -> onLoading()
        is UiState.Success -> onSuccess(state.data)
        is UiState.Error -> onError(state.message.asString(context))
    }
}

@Composable
fun DefaultLoader() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
fun ErrorContent(errorText: String, modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = errorText)
    }
}

fun <T> Flow<T>.asUiState(): Flow<UiState<T>> {
    return this
        .map<T, UiState<T>> { data ->
            UiState.Success(data)
        }
        .catch { e ->
            val errorText = UiStateError.Unknown(e.message ?: "Unknown error").toUiText()
            emit(UiState.Error(errorText))
        }
}