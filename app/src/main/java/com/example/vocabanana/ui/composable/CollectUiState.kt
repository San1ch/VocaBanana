package com.example.vocabanana.ui.composable

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import com.example.vocabanana.core.navigation.AppDestination
import com.example.vocabanana.core.presentation.UiEvent
import kotlinx.coroutines.flow.Flow

@SuppressLint("LocalContextGetResourceValueCall")
@Composable
fun CollectUiEvents(
    events: Flow<UiEvent>,
    navigateBack: () -> Unit,
    navigateTo: (AppDestination) -> Unit
) {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        events.collect { event ->
            when (event) {
                is UiEvent.ShowToast ->
                    Toast.makeText(
                        context,
                        event.uiText.asString(context),
                        Toast.LENGTH_SHORT
                    ).show()

                UiEvent.NavigateBack -> navigateBack()
                is UiEvent.NavigateTo -> navigateTo(event.destination)
            }
        }
    }
}