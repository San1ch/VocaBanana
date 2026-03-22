package com.example.vocabanana.features.init

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowRight
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun InitScreen(viewModel: InitScreenViewModel = hiltViewModel(), onFinished: () -> Unit) {
    InitContent(onFinished = {
        onFinished()
        viewModel.finishInit()
    })
}

@Composable
fun InitContent(onFinished: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Test Init Screen")
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onFinished
            ) {
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowRight, contentDescription = "Next")
            }
        }
    }
}