package com.example.vocabanana.feature.text.presentation.data

import com.example.vocabanana.R
import com.example.vocabanana.core.presentation.UiText
import com.example.vocabanana.feature.text.domain.GenerateWordsFromTextResult




sealed class GenerateWordsFromTextUiResult {
    data class NotAllNewWordsAdded(val addedCount: Int, val totalCount: Int) :
        GenerateWordsFromTextUiResult()

    data class Success(val words: List<WordUi>) : GenerateWordsFromTextUiResult()
    data class Error(val message: UiText) : GenerateWordsFromTextUiResult()
}

fun GenerateWordsFromTextResult.toUi() = when (this) {
    is GenerateWordsFromTextResult.Success.Words -> {
        GenerateWordsFromTextUiResult.Success(this.words.map { it.toUi() })
    }
    GenerateWordsFromTextResult.Success.AllWordsAlreadyExists -> {
        GenerateWordsFromTextUiResult.Error(
            UiText.StringResource(R.string.generate_words_from_text_all_words_already_exists)
        )
    }
    is GenerateWordsFromTextResult.Error -> {
        if (this is GenerateWordsFromTextResult.Error.NotAllNewWordsAdded) {
            GenerateWordsFromTextUiResult.NotAllNewWordsAdded(
                addedCount = this.addedCount,
                totalCount = this.totalCount
            )
        } else {
            GenerateWordsFromTextUiResult.Error(this.toUiText())
        }
    }

}

