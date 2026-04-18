package com.example.vocabanana.feature.text.presentation.data

import android.content.Context
import com.example.vocabanana.R
import com.example.vocabanana.core.presentation.UiText
import com.example.vocabanana.feature.text.domain.GenerateWordsFromTextResult
import com.example.vocabanana.feature.text.domain.GenerateWordsFromTextState


sealed class GenerateWordsFromTextUiResult {
    data class NotAllNewWordsAdded(val addedCount: Int, val totalCount: Int) :
        GenerateWordsFromTextUiResult()

    data class Success(val words: List<WordUi>) : GenerateWordsFromTextUiResult()
    data class Error(val message: UiText) : GenerateWordsFromTextUiResult()
}

fun GenerateWordsFromTextState.toUiState(): GenerateWordsFromTextUiState {
    return when (this) {
        is GenerateWordsFromTextState.Loading -> {
            GenerateWordsFromTextUiState.Loading(this.toUiText())
        }

        is GenerateWordsFromTextState.Success -> {
            when (val res = this.result) {
                is GenerateWordsFromTextResult.Success.Words -> {
                    GenerateWordsFromTextUiState.Success(
                        words = res.words.map { it.toUi() },
                        message = res.toUiText()
                    )
                }
                GenerateWordsFromTextResult.Success.AllWordsAlreadyExists -> {
                    GenerateWordsFromTextUiState.AllExist(this.toUiText())
                }
            }
        }

        is GenerateWordsFromTextState.Error -> {
            if (this.error is GenerateWordsFromTextResult.Error.NotAllNewWordsAdded) {
                GenerateWordsFromTextUiState.PartialSuccess(UiText.StringResource(R.string.generate_words_from_text_partial_success, this.error.addedCount, this.error.totalCount))
            } else {
                GenerateWordsFromTextUiState.Error(this.error.toUiText())
            }
        }
    }
}

private fun GenerateWordsFromTextState.toUiText(): UiText = when(this){
    GenerateWordsFromTextState.Loading.AnalyzingLexicon -> UiText.StringResource(R.string.generate_words_from_text_analyzing_lexicon)
    GenerateWordsFromTextState.Loading.PreparingText -> UiText.StringResource(R.string.generate_words_from_text_preparing_text)
    GenerateWordsFromTextState.Loading.SavingWords -> UiText.StringResource(R.string.generate_words_from_text_saving_words)
    is GenerateWordsFromTextState.Success -> result.toUiText()
    is GenerateWordsFromTextState.Error -> error.toUiText()
}

fun GenerateWordsFromTextResult.Success.toUiText(): UiText = when(this){
    GenerateWordsFromTextResult.Success.AllWordsAlreadyExists -> UiText.StringResource(R.string.generate_words_from_text_all_words_already_exists)
    is GenerateWordsFromTextResult.Success.Words -> UiText.StringResource(R.string.generate_words_from_text_success, words.size)
}