package com.example.vocabanana.feature.text.domain.usecase

import com.example.vocabanana.core.database.WordRepository
import javax.inject.Inject

class TextProcessingService @Inject constructor(
    private val wordRepository: WordRepository,
) {
    fun isTrash(word: String): Boolean {
        val w = word.trim().lowercase()

        return when {
            // 1. Metadata, Links, and Identifiers
            w.contains("http") || w.contains(".com") || w.contains("isbn") ||
                    w.contains("lccn") || w.contains("www") || w == "inc" || w == "llc" -> true

            // 2. Sound Effects & Screaming (e.g., "aaaaah", "uuugh", "mooorniiing")
            // This regex catches 3 or more repeating letters
            w.matches(Regex(".*(.)\\1\\1.*")) -> true

            // 3. Romanized Japanese Honorifics
            w.endsWith("sama") || w.endsWith("san") || w.endsWith("kun") || w.endsWith("chan") -> true

            // 4. Short Junk / Leftovers (e.g., "v", "e", "sb", "ny")
            w.length <= 2 && w != "is" && w != "am" && w != "at" -> true

            // 5. Pure numbers
            w.matches(Regex("\\d+")) -> true

            else -> false
        }
    }

    fun hyphenWordsNormalization(text: String): String {
        val regex = Regex("(?<=\\w)-(?=\\w)")
        return text.replace(regex, " ")
    }

    fun cleanText(text: String): String {
        val regex = Regex("[^a-zA-Z\\s-']")

        return text
            .lowercase()
            .replace(regex, " ")
            .replace(Regex("\\s+"), " ")
            .trim()
    }

    fun parseText(text: String): List<String> {
        return text
            .split(Regex("\\s+"))
            .map { word ->
                word.trim { !it.isLetter() }
            }
            .filter { it.isNotBlank() && !isTrash(it) }
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
