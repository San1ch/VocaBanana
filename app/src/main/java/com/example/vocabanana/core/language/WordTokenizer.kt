package com.example.vocabanana.core.language

import javax.inject.Inject
import javax.inject.Singleton

class WordTokenizer @Inject constructor() {

    fun tokenize(sentence: String): List<String> {
        val rawWords = sentence.split(Regex("[^\\p{L}'-]+"))
            .filter { it.isNotBlank() && it.length > 1 }
            .map { it.lowercase() }

        val expanded = mutableListOf<String>()

        rawWords.forEach { word ->
            when {
                word.endsWith("'re") -> {
                    expanded.add(word.dropLast(3))
                    expanded.add("are")
                }
                word.endsWith("'ve") -> {
                    expanded.add(word.dropLast(3))
                    expanded.add("have")
                }
                word.endsWith("'ll") -> {
                    expanded.add(word.dropLast(3))
                    expanded.add("will")
                }
                word.endsWith("n't") -> {
                    val stem = if (word == "can't") "can" else word.dropLast(3)
                    expanded.add(stem)
                    expanded.add("not")
                }
                // Для 's та 'd просто беремо основу (Aman's -> Aman)
                word.contains("'") -> {
                    val parts = word.split("'")
                    if (parts[0].length > 1) expanded.add(parts[0])
                }
                else -> expanded.add(word)
            }
        }
        return expanded.filter { it.length > 1 }
    }
}