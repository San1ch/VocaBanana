package com.example.vocabanana.ui.composable

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

object DpSizes{
    val micro = 4.dp
    val small = 8.dp
    val medium = 16.dp
    val large = 24.dp
}


@Composable
fun SpacerMedium() {
    Spacer(modifier = Modifier.height(DpSizes.medium))
}

@Composable
fun SpacerSmall() {
    Spacer(modifier = Modifier.height(DpSizes.small))
}
@Composable
fun SpacerMicro() {
    Spacer(modifier = Modifier.height(DpSizes.micro))
}

@Composable
fun SpacerLarge() {
    Spacer(modifier = Modifier.height(DpSizes.large))
}

