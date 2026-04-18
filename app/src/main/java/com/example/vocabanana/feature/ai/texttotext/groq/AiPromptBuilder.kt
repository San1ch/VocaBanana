package com.example.vocabanana.feature.ai.texttotext.groq

import com.example.vocabanana.core.domain.model.SentenceWithItsWords
import com.example.vocabanana.core.word.domain.model.PartOfSpeech
import com.example.vocabanana.feature.text.domain.AiJsonKeys
import javax.inject.Inject

class AiPromptBuilder @Inject constructor() {

    // Prompt with instruction and data
    fun createSentencesToWordsPrompt(list: List<SentenceWithItsWords>): String {
        val systemPrompt = buildSystemPrompt()
        val list = list.joinToString(separator = "\n\n") { item ->
            "Words: ${item.words.joinToString(", ")}\nContext: ${item.sentence}"
        }
        return "$systemPrompt\n\n$list"
    }

    // Prompt instruction builder
    private fun buildSystemPrompt(): String {
        val allowedPos = PartOfSpeech.entries
            .filter { it != PartOfSpeech.UNKNOWN }
            .joinToString { it.shortName }

        return """
        You are a linguistic expert analyzing English vocabulary in context.
        
        TASK:
        For each word in the "Words" list, use the provided "Context" to determine its true Lemma and Part of Speech (POS).
        
        RULES:
        1. Word POS (${AiJsonKeys.WORD_PART_OF_SPEECH}): Identify the part of speech of the word EXACTLY as it is used in the sentence.
        2. Lemma POS (${AiJsonKeys.LEMMA_PART_OF_SPEECH}): Identify the primary part of speech of the resulting Lemma.
        3. Normalization: Ensure the Lemma is in its base form (infinitive for verbs, singular for nouns).
        4. Homonyms: If the context clarifies a specific meaning of a homonym (e.g., 'object' as a verb vs 'object' as a noun), ensure both POS fields reflect the usage in the provided context.
        5. Fixing: If word have mistakes like "buildE", you should return "build" or any other correct form by CONTEXT. Incorrect words shouldn't exist, only fixed forms.

        OUTPUT FORMAT:
        Return ONLY a JSON array of objects. Use these keys:
        - "${AiJsonKeys.WORD}" The word from the text.
        - "${AiJsonKeys.LEMMA}" The base form (lemma).
        - "${AiJsonKeys.WORD_PART_OF_SPEECH}" Part of speech of the word in context.
        - "${AiJsonKeys.LEMMA_PART_OF_SPEECH}" Part of speech of the lemma.

        ALLOWED POS VALUES:
        [$allowedPos]
        
        Example JSON:
        [
          {
            "${AiJsonKeys.WORD}": "running",
            "${AiJsonKeys.LEMMA}": "run",
            "${AiJsonKeys.WORD_PART_OF_SPEECH}": "verb",
            "${AiJsonKeys.LEMMA_PART_OF_SPEECH}": "verb"
          }
        ]
        """.trimIndent()
    }
}