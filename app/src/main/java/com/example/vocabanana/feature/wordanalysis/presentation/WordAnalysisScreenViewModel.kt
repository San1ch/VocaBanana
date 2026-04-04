package com.example.vocabanana.feature.wordanalysis.presentation

import androidx.lifecycle.viewModelScope
import com.example.vocabanana.core.database.text.repository.TextRepository
import com.example.vocabanana.core.presentation.BaseViewModel
import com.example.vocabanana.core.presentation.asUiState
import com.example.vocabanana.core.presentation.uistate.UiState
import com.example.vocabanana.feature.text.presentation.data.toPreview
import com.example.vocabanana.feature.wordanalysis.domain.GetInfoFromTextUseCase
import com.example.vocabanana.feature.wordanalysis.domain.WordTestCorrectionCenter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WordAnalysisScreenViewModel @Inject constructor(
    private val textRepository: TextRepository,
    private val wordTestCorrectionCenter: WordTestCorrectionCenter,
    private val getInfoFromTextUseCase: GetInfoFromTextUseCase
) : BaseViewModel() {
    val text =
        textRepository.getTexts()
            .map { list -> list.map { it.toPreview() } }
            .asUiState()
            .stateIn(
                viewModelScope,
                SharingStarted.Eagerly,
                UiState.Loading
            )

    fun startTest(textId: Int) = viewModelScope.launch(Dispatchers.IO) {
        getInfoFromTextUseCase(textId)
    }

    val testStage = wordTestCorrectionCenter.observeCurrentTestStage()
}

