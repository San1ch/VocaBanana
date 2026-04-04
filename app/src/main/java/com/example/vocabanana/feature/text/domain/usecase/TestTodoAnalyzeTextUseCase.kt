package com.example.vocabanana.feature.text.domain.usecase

import javax.inject.Inject

class TestTodoAnalyzeTextUseCase @Inject constructor() {

    data class AnalysisResult(
        val totalWords: Int,
        val sEnding: Int,      // plural / 3rd person (cats, works)
        val edEnding: Int,     // past tense (worked)
        val ingEnding: Int,    // gerund (working)
        val iesEnding: Int,    // y -> ies (studies)
        val totalProblematic: Int,
        val percentage: Float
    )

    operator fun invoke(text: String): AnalysisResult {
        val words = text.lowercase()
            .split(Regex("[^\\p{L}]+"))
            .filter { it.length > 2 }

        val total = words.size
        var s = 0
        var ed = 0
        var ing = 0
        var ies = 0

        words.forEach { word ->
            when {
                word.endsWith("ies") -> ies++
                word.endsWith("ed") && word.length > 3 -> ed++
                word.endsWith("ing") && word.length > 4 -> ing++
                word.endsWith("s") && !word.endsWith("ss") -> s++
            }
        }

        val problematic = s + ed + ing + ies
        val percent = if (total > 0) (problematic.toFloat() / total) * 100f else 0f

        return AnalysisResult(total, s, ed, ing, ies, problematic, percent)
    }
}