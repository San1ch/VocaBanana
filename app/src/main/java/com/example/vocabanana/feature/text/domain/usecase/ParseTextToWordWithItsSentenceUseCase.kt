package com.example.vocabanana.feature.text.domain.usecase

import com.example.vocabanana.core.database.WordRepository
import javax.inject.Inject

class TextProcessingService @Inject constructor(
    private val wordRepository: WordRepository,
) {

    fun cleanText(text: String): String {
        val regex = Regex("[^a-zA-Z0-9\\s-']")

        return text
            .lowercase()
            .replace(regex, " ")
            .replace(Regex("\\s+"), " ")
            .trim()
    }

    fun parseText(text: String): List<String> {
        return text
            .split(Regex("\\s+"))
            .filter { it.isNotBlank() }
    }
}
