package com.example.vocabanana.feature.text.domain

import com.example.vocabanana.core.domain.model.SentenceWithItsWords
import com.example.vocabanana.feature.ai.texttotext.groq.AiPromptBuilder
import javax.inject.Inject

class BuildPromptsFromSentenceBatchesUseCase @Inject constructor(
    private val promptBuilder: AiPromptBuilder
) {
    operator fun invoke(
        sentences: List<SentenceWithItsWords>,
        wordsPerPrompt: Int,
    ): List<String> {
        val chunks = mutableListOf<List<SentenceWithItsWords>>()
        val currentChunk = mutableListOf<SentenceWithItsWords>()
        var currentWords = 0

        for(sentence in sentences){
            val size = sentence.words.size

            //  Filter: If I add this sentence, will it exceed the limit?
            //  And checking for overflowing by one element
            if(currentWords + size > wordsPerPrompt && currentChunk.isNotEmpty()){
                chunks.add(currentChunk.toList())
                currentChunk.clear()
                currentWords = 0
            }

            currentChunk.add(sentence)
            currentWords += size
        }

        if (currentChunk.isNotEmpty()) {
            chunks.add(currentChunk.toList())
        }

        return chunks.map { buildPrompts(it) }
    }

    private fun buildPrompts(currentSentences: List<SentenceWithItsWords>): String {
        return promptBuilder.createSentencesToWordsPrompt(currentSentences)
    }
}