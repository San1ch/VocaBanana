package com.example.vocabanana.feature.vocabulary.presentation

import androidx.lifecycle.ViewModel
import com.example.vocabanana.core.repository.wordrepository.WordFormRepository
import com.example.vocabanana.core.repository.wordrepository.WordRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class VocabularyScreenViewModel @Inject constructor(
    private val wordRepository: WordRepository,
    private val wordFormRepository: WordFormRepository
) : ViewModel() {

    val words = wordRepository.getAllWords()

}


data class UiWord(
    val id: Long,
    val word: String,
    val whenAdded: Long
)