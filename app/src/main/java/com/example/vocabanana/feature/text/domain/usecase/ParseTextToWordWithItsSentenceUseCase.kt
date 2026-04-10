package com.example.vocabanana.feature.text.domain.usecase

import com.example.vocabanana.core.database.WordRepository
import javax.inject.Inject

class TextProcessingService @Inject constructor(
    private val wordRepository: WordRepository,
) {
    fun hyphenWordsNormalization(text: String): String {
        val regex = Regex("(?<=\\w)-(?=\\w)")
        return text.replace(regex, " ")
    }
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

    fun normalizeGrammar(text: String): String {
        var result = text.replace('’', '\'').replace('`', '\'')

        val directExpansions = mapOf(
            "'re" to " are",
            "'ve" to " have",
            "'ll" to " will",
            "n't" to " not",
            "'m" to " am"
        )

        directExpansions.forEach { (shorthand, full) ->
            result = result.replace(shorthand, full, ignoreCase = true)
        }

        result = result.replace("['](s|d|t|all)\\b".toRegex(RegexOption.IGNORE_CASE), "")

        return result
    }
}
