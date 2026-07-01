package com.san1ch.vocabanana.feature.text.presentation.mapper

import com.san1ch.vocabanana.core.essentials.resources.featureproviders.TextStringProvider
import com.san1ch.vocabanana.feature.text.domain.GenerateWordsFromTextResult
import com.san1ch.vocabanana.feature.text.domain.GenerateWordsFromTextState
import com.san1ch.vocabanana.feature.text.presentation.model.GenerateWordsFromTextUiResult
import com.san1ch.vocabanana.feature.text.presentation.model.GenerateWordsFromTextUiState
import javax.inject.Inject

class GenerateWordsFromTextUiMapper @Inject constructor(
    private val stringProvider: TextStringProvider,
) {

    fun map(
        state: GenerateWordsFromTextState
    ): GenerateWordsFromTextUiState {
        return when (state) {

            is GenerateWordsFromTextState.Loading.PreparingText -> {
                GenerateWordsFromTextUiState.Loading.PreparingText(
                    message = stringProvider.generateWordsPreparingText
                )
            }

            is GenerateWordsFromTextState.Loading.AnalyzingLexicon -> {
                GenerateWordsFromTextUiState.Loading.AnalyzingLexicon(
                    message = stringProvider.generateWordsAnalyzingLexicon
                )
            }

            is GenerateWordsFromTextState.Loading.SavingWords -> {
                GenerateWordsFromTextUiState.Loading.SavingWords(
                    message = stringProvider.generateWordsSavingWords
                )
            }

            is GenerateWordsFromTextState.Success -> {
                val result = mapSuccess(state.result)

                GenerateWordsFromTextUiState.Success(
                    result = result,
                    message = result.message
                )
            }

            is GenerateWordsFromTextState.Error -> {
                val error = mapError(state.error)

                GenerateWordsFromTextUiState.Error(
                    error = error,
                    message = error.message
                )
            }
        }
    }

    private fun mapSuccess(
        success: GenerateWordsFromTextResult.Success
    ): GenerateWordsFromTextUiResult.Success {
        return when (success) {

            is GenerateWordsFromTextResult.Success.Words -> {
                GenerateWordsFromTextUiResult.Success.Words(
                    words = success.words,
                    message = stringProvider.generateWordsSuccess
                )
            }

            GenerateWordsFromTextResult.Success.AllWordsAlreadyExists -> {
                GenerateWordsFromTextUiResult.Success.AllWordsAlreadyExists(
                    message = stringProvider.generateWordsAllExists
                )
            }
        }
    }

    private fun mapError(
        error: GenerateWordsFromTextResult.Error
    ): GenerateWordsFromTextUiResult.Error {
        return when (error) {

            GenerateWordsFromTextResult.Error.NetworkError -> {
                GenerateWordsFromTextUiResult.Error.NetworkError(
                    message = stringProvider.generateWordsNetworkError
                )
            }

            GenerateWordsFromTextResult.Error.InvalidApiKey -> {
                GenerateWordsFromTextUiResult.Error.InvalidApiKey(
                    message = stringProvider.generateWordsInvalidApiKey
                )
            }

            GenerateWordsFromTextResult.Error.RateLimitExceeded -> {
                GenerateWordsFromTextUiResult.Error.RateLimitExceeded(
                    message = stringProvider.generateWordsRateLimitExceeded
                )
            }

            GenerateWordsFromTextResult.Error.ServerError -> {
                GenerateWordsFromTextUiResult.Error.ServerError(
                    message = stringProvider.generateWordsServerError
                )
            }

            is GenerateWordsFromTextResult.Error.NotAllNewWordsAdded -> {
                GenerateWordsFromTextUiResult.Error.NotAllNewWordsAdded(
                    addedCount = error.addedCount,
                    totalCount = error.totalCount,
                    message = stringProvider.generateWordsNotAllNewWordsAdded(
                        error.addedCount,
                        error.totalCount
                    )
                )
            }

            GenerateWordsFromTextResult.Error.TextNotFound -> {
                GenerateWordsFromTextUiResult.Error.TextNotFound(
                    message = stringProvider.generateWordsTextNotFound
                )
            }

            is GenerateWordsFromTextResult.Error.Unknown -> {
                GenerateWordsFromTextUiResult.Error.Unknown(
                    rawMessage = error.message,
                    message = stringProvider.generateWordsUnknownError(error.message)
                )
            }
        }
    }
}