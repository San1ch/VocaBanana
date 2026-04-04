package com.example.vocabanana.core.language

import javax.inject.Inject

class WordNormalizer @Inject constructor() {
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