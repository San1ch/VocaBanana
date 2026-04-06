package com.example.vocabanana.core.language

import javax.inject.Inject

class TextProcessor @Inject constructor() {
    fun parseToSentences(text: String): List<String> {
        val sentenceRegex = Regex("(?<=[.!?])\\s+(?=[\\p{Lu}\\p{L}])|\\n+")
        return text.split(sentenceRegex)
            .filter { it.isNotBlank() }
            .map { it.trim() }
    }
    fun tokenizeSentence(sentence: String): List<String> {
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
                word.contains("'") -> {
                    val parts = word.split("'")
                    if (parts[0].length > 1) expanded.add(parts[0])
                }
                else -> expanded.add(word)
            }
        }

        return expanded.filter { it.length > 1 }
    }
    fun normalize(word: String): String {
        val w = word.lowercase().trim()
        if (w.length <= 4) return w

        return when {
            w.endsWith("ies") -> w.dropLast(3) + "y"
            w.endsWith("ed") -> {
                val base = w.dropLast(2)
                when {
                    w.endsWith("ied") -> base + "y"

                    base.length > 1 && base.last() == base[base.length - 2] -> base.dropLast(1)

                    base.endsWith("sh") || base.endsWith("ch") ||
                            base.endsWith("x") || base.endsWith("ss") -> base

                    else -> base + "e"
                }
            }
            w.endsWith("ing") && w.length > 5 -> {
                val base = w.dropLast(3)

                when {
                    base.length > 1 && base.last() == base[base.length - 2] &&
                            !listOf('s', 'f', 'l', 'z').contains(base.last()) -> {
                        base.dropLast(1)
                    }

                    base.last() in "bcdfghjklmnpqrstvez" -> {
                        base + "e"
                    }

                    else -> base
                }
            }
            w.endsWith("s") && !w.endsWith("ss") -> w.dropLast(1)
            else -> w
        }
    }

}