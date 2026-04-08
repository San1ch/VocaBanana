package com.example.vocabanana.feature.text.domain.usecase

import com.example.vocabanana.core.language.TextProcessor
import com.example.vocabanana.core.domain.model.SentenceWithItsWords
import com.example.vocabanana.core.database.WordRepository
import javax.inject.Inject

class TextProcessingService @Inject constructor(
    private val wordRepository: WordRepository,
){
    suspend fun parseTextToWordWithItsSentence(text: String): List<SentenceWithItsWords> {




    }

}
