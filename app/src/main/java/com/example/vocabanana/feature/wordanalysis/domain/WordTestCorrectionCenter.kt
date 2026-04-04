package com.example.vocabanana.feature.wordanalysis.domain

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

class WordTestCorrectionCenter @Inject constructor() {
    private var currentStage =
        MutableStateFlow<TestWordCorrectionStage>(TestWordCorrectionStage.Sleep)

    fun changeWorkingStage(
        currentBatchStage: Int? = null,
        maxBatchStage: Int? = null,
        currentPromptStage: Int? = null,
        maxPromptStage: Int? = null
    ) {
        val cs = currentStage.value

        currentStage.value = TestWordCorrectionStage.Working(
            currentBatchStage = currentBatchStage ?: (cs as? TestWordCorrectionStage.Working)?.currentBatchStage ?: 0,
            maxBatchStage = maxBatchStage ?: (cs as? TestWordCorrectionStage.Working)?.maxBatchStage ?: 0,
            currentPromptStage = currentPromptStage ?: (cs as? TestWordCorrectionStage.Working)?.currentPromptStage ?: 0,
            maxPromptStage = maxPromptStage ?: (cs as? TestWordCorrectionStage.Working)?.maxPromptStage ?: 0
        )
    }

    fun observeCurrentTestStage(): Flow<TestWordCorrectionStage> {
        return currentStage
    }
}

sealed class TestWordCorrectionStage {
    object Sleep : TestWordCorrectionStage()
    data class Working(
        val currentBatchStage: Int,
        val maxBatchStage: Int,
        val currentPromptStage: Int,
        val maxPromptStage: Int
    ) : TestWordCorrectionStage()
}