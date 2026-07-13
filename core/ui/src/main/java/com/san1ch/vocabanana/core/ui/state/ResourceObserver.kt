package com.san1ch.vocabanana.core.ui.state

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.san1ch.vocabanana.core.ui.R
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

@Composable
fun <T : Any> ResourceObserver(
    state: Resource<T>,
    onLoading: (@Composable () -> Unit)? = null,
    onError: (@Composable (String) -> Unit)? = null,
    onEmpty: (@Composable () -> Unit)? = null,
    onSuccess: (@Composable (T) -> Unit),
) {
    when (state) {
        is Resource.Loading -> {
            if (onLoading != null) onLoading() else DefaultLoader()
        }
        is Resource.Empty -> {
            if (onEmpty != null) onEmpty() else DefaultLoaderEmpty()
        }
        is Resource.Success -> {
            onSuccess(state.data)
        }
        is Resource.Error -> {
            val message = state.error.message ?: "Unknown error"
            if (onError != null) onError(message) else ErrorContent(errorText = message)
        }
    }
}

@Composable
fun DefaultLoader() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
fun DefaultLoaderEmpty() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = stringResource(R.string.no_data))
    }
}

@Composable
fun ErrorContent(errorText: String, modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = errorText)
    }
}

fun <T> Flow<T>.asResource(): Flow<Resource<T>> = this
    .map<T, Resource<T>> { data ->
        Resource.Success(data)
    }.onStart {
        emit(Resource.Loading)
    }
    .catch { e ->
        val errorText = ResourceError.Unknown(e.message ?: "Unknown error")
        emit(Resource.Error(errorText))
    }
