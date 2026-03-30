package com.example.vocabanana.feature.vocabulary.presentation

import com.example.vocabanana.core.presentation.BaseViewModel
import com.example.vocabanana.feature.word.domain.WordFormRepository
import com.example.vocabanana.feature.word.domain.WordRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class VocabularyScreenViewModel @Inject constructor(
    private val wordRepository: WordRepository,
    private val wordFormRepository: WordFormRepository
) : BaseViewModel() {

    val words = wordRepository.getAllWords()

}


data class UiWord(
    val id: Long,
    val word: String,
    val whenAdded: Long
)