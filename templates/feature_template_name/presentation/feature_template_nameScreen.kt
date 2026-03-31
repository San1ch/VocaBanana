package com.example.vocabanana.feature.<low_feature>.presentation


import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.vocabanana.core.navigation.AppDestination
import com.example.vocabanana.ui.composable.CollectUiEvents



@Composable
fun <feature>Screen(
    viewModel: <feature>ScreenViewModel = hiltViewModel(),
navigateTo: (AppDestination) -> Unit,
navigateBack: () -> Unit,
) {
    CollectUiEvents(
        events = viewModel.events,
        navigateBack = navigateBack,
        navigateTo = {})
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun <feature>Content(
) {

}

@Preview
@Composable
fun <feature>ScreenPreview() {
    <feature>Content()
}
