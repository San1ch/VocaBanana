package com.san1ch.vocabanana.feature.text.presentation.model

import com.san1ch.vocabanana.core.essentials.model.word.WordDomain

sealed class GenerateWordsFromTextUiState {

    sealed class Loading(
        open val message: String,
    ) : GenerateWordsFromTextUiState() {

        data class PreparingText(
            override val message: String,
        ) : Loading(message)

        data class AnalyzingLexicon(
            override val message: String,
        ) : Loading(message)

        data class SavingWords(
            override val message: String,
        ) : Loading(message)
    }

    data class Success(
        val result: GenerateWordsFromTextUiResult.Success,
        val message: String,
    ) : GenerateWordsFromTextUiState()

    data class Error(
        val error: GenerateWordsFromTextUiResult.Error,
        val message: String,
    ) : GenerateWordsFromTextUiState()
}

sealed class GenerateWordsFromTextUiResult {

    sealed class Success(
        open val message: String,
    ) : GenerateWordsFromTextUiResult() {

        data class Words(
            val words: List<WordDomain>,
            override val message: String,
        ) : Success(message)

        data class AllWordsAlreadyExists(
            override val message: String,
        ) : Success(message)
    }

    sealed class Error(
        open val message: String,
    ) : GenerateWordsFromTextUiResult() {

        data class NetworkError(
            override val message: String,
        ) : Error(message)

        data class InvalidApiKey(
            override val message: String,
        ) : Error(message)

        data class RateLimitExceeded(
            override val message: String,
        ) : Error(message)

        data class ServerError(
            override val message: String,
        ) : Error(message)

        data class NotAllNewWordsAdded(
            val addedCount: Int,
            val totalCount: Int,
            override val message: String,
        ) : Error(message)

        data class TextNotFound(
            override val message: String,
        ) : Error(message)

        data class Unknown(
            val rawMessage: String,
            override val message: String,
        ) : Error(message)
    }
}
