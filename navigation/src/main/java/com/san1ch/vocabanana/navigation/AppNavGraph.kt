package com.san1ch.vocabanana.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.san1ch.vocabanana.feature.debug.presentation.DebugScreen
import com.san1ch.vocabanana.feature.init.presentation.InitScreen
import com.san1ch.vocabanana.feature.main.presentation.MainScreen
import com.san1ch.vocabanana.feature.mainsettings.presentation.MainSettingsScreen
import com.san1ch.vocabanana.feature.text.presentation.AddTextScreen
import com.san1ch.vocabanana.feature.text.presentation.textlist.screen.TextListScreen
import com.san1ch.vocabanana.feature.vocabulary.presentation.NewWordListScreen
import com.san1ch.vocabanana.feature.vocabulary.presentation.vocabularyscreenpages.VocabularyScreen

fun NavGraphBuilder.buildAppNavGraph() {
    composable<InitRoute> { InitScreen() }
    composable<MainRoute> { MainScreen() }
    composable<TextListRoute> { TextListScreen() }
    composable<AddTextRoute> { AddTextScreen() }
    composable<VocabularyRoute> { VocabularyScreen() }
    composable<MainSettingsRoute> { MainSettingsScreen() }
    composable<DebugRoute> { DebugScreen() }
    composable<NewWordListRoute> { NewWordListScreen() }
}
