package com.example.vocabanana.feature.text.presentation

import com.example.vocabanana.core.domain.model.ValidateResult
import com.example.vocabanana.feature.text.domain.AiWordDto
import com.example.vocabanana.feature.text.domain.GenerateWordsFromTextResult
import com.example.vocabanana.feature.word.domain.model.PartOfSpeech
import com.example.vocabanana.feature.word.domain.model.WordDomain
import com.example.vocabanana.feature.word.domain.model.WordFormDomain
import javax.inject.Inject

class AiDtoToWordsUseCase @Inject constructor(
) {
    operator fun invoke(dtos: List<AiWordDto>): GenerateWordsFromTextResult {
        if (dtos.isEmpty()) {
            return GenerateWordsFromTextResult.Error.Unknown("No words generated")
        }
        val wordsToSave = dtos.mapNotNull { dto ->
            // 1. Clean form and lemma
            val cleanLemma = dto.lemma.lowercase().trim()
            val cleanWord = dto.word.lowercase().trim()

            // 2. Remove form if form is lemma
            val rawForms = if (cleanWord != cleanLemma) {
                listOf(
                    WordFormDomain.create(
                        wordId = 0, form = cleanWord,
                        partOfSpeech = PartOfSpeech.fromShortName(dto.wordPos)
                    )
                )
            } else emptyList()

            // 3. Validate forms
            val validatedForms = rawForms.mapNotNull { result ->
                if (result is ValidateResult.Success) result.value else null
            }

            // 4. Create word
            val wordResult = WordDomain.create(
                lemma = cleanLemma,
                partOfSpeech = PartOfSpeech.fromShortName(dto.lemmaPos),
                forms = validatedForms

            )

            if (wordResult is ValidateResult.Success)
                wordResult.value
            else null
        }
        return if (wordsToSave.isNotEmpty()) {
            GenerateWordsFromTextResult.Success.Words(wordsToSave)
        }else{
            GenerateWordsFromTextResult.Error.Unknown("No words generated")
        }
    }
}