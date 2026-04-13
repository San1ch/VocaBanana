package com.example.vocabanana.feature.text.domain

import com.example.vocabanana.core.domain.model.ValidateResult
import com.example.vocabanana.feature.word.domain.model.PartOfSpeech
import com.example.vocabanana.feature.word.domain.model.WordDomain
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
            val forms = if (cleanWord != cleanLemma) {
                listOf(cleanWord)
            } else emptyList()

            // 4. Create word
            val wordResult = WordDomain.create(
                lemma = cleanLemma,
                partOfSpeech = PartOfSpeech.fromShortName(dto.lemmaPos),
                forms = forms

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