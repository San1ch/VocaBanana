package com.san1ch.vocabanana.feature.vocabulary.presentation

import androidx.compose.ui.graphics.Color
import com.san1ch.vocabanana.core.ui.theme.MileStoneColor

enum class VocabMilestone(val label: String, val threshold: Int, val color: Color) {
    NOVICE("Novice", 100, MileStoneColor.Novice),
    LEARNER("Learner", 500, MileStoneColor.Learner),
    FLUENT("Fluent", 2000, MileStoneColor.Fluent),
    PROFESSIONAL("Expert", 5000, MileStoneColor.Professional),
    MASTERY("Mastery", 10000, MileStoneColor.Mastery),
    ;

    companion object {
        fun getNext(currentCount: Int) = entries.firstOrNull { it.threshold > currentCount } ?: MASTERY

        fun getCurrent(currentCount: Int) = entries.lastOrNull { it.threshold <= currentCount } ?: NOVICE
    }
}

data class VocabularyStats(
    val totalLemmas: Int = 0,
    val known: Int = 0,
    val learning: Int = 0,
    val notKnown: Int = 0,
    val ignored: Int = 0,
)
