package com.example.vocabanana.core.language

object WordNormalizer {
    private val protectedWords = setOf("is", "as", "has", "was", "less", "mess", "gas", "bus", "this")

    fun normalize(word: String): String {
        val w = word.lowercase().trim()

        if (w.length <= 3 || w in protectedWords) return w

        return when {
            // 2. studies -> study (ies -> y)
            w.endsWith("ies") && w.length > 4 ->
                w.dropLast(3) + "y"

            // 3. cats -> cat (s -> base)
            // Перевірка !endsWith("ss"), щоб не зіпсувати "class", "glass"
            w.endsWith("s") && !w.endsWith("ss") && w.length > 4 ->
                w.dropLast(1)

            // 4. worked -> work (ed -> base)
            w.endsWith("ed") && w.length > 4 ->
                w.dropLast(2)

            // 5. playing -> play (ing -> base)
            w.endsWith("ing") && w.length > 5 ->
                w.dropLast(3)

            else -> w
        }
    }
}