package com.san1ch.vocabanana.core.ui.compose

import android.annotation.SuppressLint
import android.content.Intent
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri
import com.san1ch.vocabanana.core.ui.UiEvent
import kotlinx.coroutines.flow.Flow

@SuppressLint("LocalContextGetResourceValueCall")
@Composable
fun CollectUiEvents(
    events: Flow<UiEvent>,
) {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        events.collect { event ->
            when (event) {
                is UiEvent.ShowToast ->
                    Toast.makeText(
                        context,
                        event.message,
                        Toast.LENGTH_SHORT
                    ).show()
                is UiEvent.OpenUrl -> {
                    try {
                        val intent = Intent(
                            Intent.ACTION_VIEW,
                            event.url.toUri()
                        )
                        context.startActivity(intent)
                    } catch (e: Exception) {
                        Toast.makeText(context, "Cannot open link", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}