package com.example.vocabanana.feature.wordanalysis.domain

import android.content.Context
import android.util.Log
import com.example.vocabanana.core.database.text.repository.TextRepository
import com.example.vocabanana.feature.text.domain.usecase.ParseTextToWordWithSentenceUseCase
import com.google.gson.GsonBuilder
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class GetInfoFromTextUseCase @Inject constructor(
    private val parseTextToWordWithSentenceUseCase: ParseTextToWordWithSentenceUseCase,
    private val textRepository: TextRepository,
    private val aiInputCenter: AiInputCenter,
    @ApplicationContext private val context: Context
) {
    suspend operator fun invoke(textId: Int) {
        val sentencesWithWords =
            parseTextToWordWithSentenceUseCase(textRepository.getTextById(textId).content)
        val logs = aiInputCenter.startTests(sentencesWithWords, 30, 50, 70)
        saveLogsToFile(context, logs)
    }

    fun saveLogsToFile(context: Context, logs: List<WordInfoValidationLog>) {
        val json = GsonBuilder().setPrettyPrinting().create().toJson(logs)
        val fileName = "ai_test_${System.currentTimeMillis()}.json"

        context.openFileOutput(fileName, Context.MODE_PRIVATE).use {
            it.write(json.toByteArray())
        }
        Log.i("AI_TEST", "Logs saved to $fileName. Use Device Explorer to pull it.")
    }
}