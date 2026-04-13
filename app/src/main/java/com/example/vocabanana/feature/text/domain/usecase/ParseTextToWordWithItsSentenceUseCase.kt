package com.example.vocabanana.feature.text.domain.usecase

import com.example.vocabanana.core.database.WordRepository
import javax.inject.Inject

class TextProcessingService @Inject constructor(
    private val wordRepository: WordRepository,
) {
    val stopWords = setOf(
        // Articles & Conjunctions
        "the", "a", "an", "and", "or", "but", "if", "then", "else", "when",
        "while", "because", "as", "until", "although", "since", "nor",

        // Prepositions
        "with", "from", "to", "for", "in", "on", "at", "by", "of", "about",
        "into", "through", "during", "before", "after", "above", "below",
        "under", "between", "among", "against", "toward", "upon", "onto",

        // Pronouns
        "me", "you", "him", "her", "she", "he", "it", "we", "they", "us", "them",
        "my", "your", "his", "its", "our", "their", "mine", "yours", "hers", "ours", "theirs",
        "myself", "yourself", "himself", "herself", "itself", "ourselves", "themselves",
        "who", "whom", "whose", "which", "that", "this", "those", "these",
        "someone", "something", "anybody", "anyway", "anyone", "anything", "everyone", "everybody",

        // Auxiliary Verbs / Common Modals
        "is", "am", "are", "was", "were", "be", "been", "being",
        "have", "has", "had", "do", "does", "did", "done", "doing",
        "can", "could", "will", "would", "shall", "should", "may", "might", "must",

        // Common Adverbs/Markers
        "not", "no", "yes", "too", "very", "just", "only", "well", "even", "now", "then", "there", "here"
    )

    fun isTrash(word: String): Boolean {
        if (word.length <= 2 && word !in listOf("is", "am", "at")) return true
        if (word.firstOrNull()?.isDigit() == true) return true

        val w = word.lowercase()
        return w.contains("http")
                || w.contains("com")
                || w.contains("www")
                || w.contains("the")
                || w.contains("a")
                || w.contains("an")
    }

    fun String.removeSurroundingPunctuation(): String {
        return this.dropWhile { !it.isLetter() }
            .dropLastWhile { !it.isLetter() }
    }

    fun prepareText(text: String): List<String> {
        // 1. Normalize quotes first
        val normalized = text.replace('’', '\'').replace('`', '\'')

        val words = normalized.split(Regex("[^a-zA-Z']+"))

        return words.asSequence()
            .filter { it.isNotBlank() }
            .filter { !stopWords.contains(it) } // the, a, an, and, or, but, if, then...
            .flatMap { word ->
                val w = word.lowercase()
                when {
                    w.endsWith("n't") -> {
                        if (w == "can't") listOf("can", "not")
                        else listOf(w.removeSuffix("n't"), "not")
                    }

                    w.endsWith("'re") -> listOf(w.removeSuffix("'re"), "are")
                    w.endsWith("'ve") -> listOf(w.removeSuffix("'ve"), "have")
                    w.endsWith("'ll") -> listOf(w.removeSuffix("'ll"), "will")
                    w.endsWith("'m") -> listOf(w.removeSuffix("'m"), "am")
                    // Strips 's, 'd, 't, 'all
                    w.contains("'") -> listOf(w.substringBefore("'"))
                    else -> listOf(w)
                }

            }
            .filter { it.isNotEmpty() && !isTrash(it) }
            .distinct()
            .toList()
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
