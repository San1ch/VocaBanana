package com.example.vocabanana.ui.custom

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import com.example.vocabanana.core.presentation.UiEvent
import kotlinx.coroutines.flow.Flow
import androidx.compose.ui.res.stringResource

@SuppressLint("LocalContextGetResourceValueCall")
@Composable
fun CollectUiEvents(
    events: Flow<UiEvent>
) {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        events.collect { event ->
            when (event) {
                is UiEvent.ShowToast ->
                    Toast.makeText(
                        context,
                        context.getString(
                            event.message.resId,
                            *event.message.args.toTypedArray()
                        ),
                        Toast.LENGTH_SHORT
                    ).show()
            }
        }
    }
}