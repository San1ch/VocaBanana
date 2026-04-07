package com.example.vocabanana.feature.text.domain

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import android.util.Log
import javax.inject.Inject
import kotlinx.serialization.json.Json


@Serializable
data class AiWordDto(
    @SerialName(AiJsonKeys.WORD) val word: String,
    @SerialName(AiJsonKeys.LEMMA) val lemma: String,
    @SerialName(AiJsonKeys.WORD_PART_OF_SPEECH) val wordPos: String,
    @SerialName(AiJsonKeys.LEMMA_PART_OF_SPEECH) val lemmaPos: String
)

class JsonTextToWordsParser @Inject constructor() {
    private val jsonConfig = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    fun parse(rawJson: String): List<AiWordDto> {
        return try {
            val cleanInput = rawJson.trim()
                .removeSurrounding("```json", "```")
                .trim()

            val arrayRegex = Regex("\\[[\\s\\S]*?]")
            val matches = arrayRegex.findAll(cleanInput)

            val allWords = mutableListOf<AiWordDto>()

            for (match in matches) {
                val jsonPart = match.value
                val partWords = jsonConfig.decodeFromString<List<AiWordDto>>(jsonPart)
                allWords.addAll(partWords)
            }

            if (allWords.isEmpty() && cleanInput.isNotEmpty()) {
                Log.w("AiResponseParser", "No JSON arrays found in input: $cleanInput")
            }

            allWords
        } catch (e: Exception) {
            Log.e("AiResponseParser", "Failed to parse AI response: ${e.message}")
            emptyList()
        }
    }
}

object AiJsonKeys {
    const val WORD = "w"
    const val LEMMA = "l"
    const val WORD_PART_OF_SPEECH = "wp"
    const val LEMMA_PART_OF_SPEECH = "lp"
}
