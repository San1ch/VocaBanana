package com.example.vocabanana.feature.vocabulary.presentation

import androidx.lifecycle.viewModelScope
import com.example.vocabanana.core.database.word.repository.WordRepository
import com.example.vocabanana.core.presentation.BaseViewModel
import com.example.vocabanana.core.presentation.asUiState
import com.example.vocabanana.core.presentation.uistate.UiState
import com.example.vocabanana.feature.word.domain.PartOfSpeech
import com.example.vocabanana.feature.word.domain.WordDomain
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class VocabularyScreenViewModel @Inject constructor(
    private val wordRepository: WordRepository,
) : BaseViewModel() {

    val words = wordRepository.getAllWords()
        .map{ list -> list.map { it.toUi() } }
        .asUiState()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            UiState.Success(emptyList())
        )

}


data class UiWord(
    val id: Int,
    val word: String,
    val whenAdded: Long,
    val partOfSpeech: String
)

fun PartOfSpeech.toUi(): String = when (this) {
    PartOfSpeech.NOUN -> "noun"
    PartOfSpeech.VERB -> "verb"
    PartOfSpeech.ADJECTIVE -> "adj"
    PartOfSpeech.ADVERB -> "adverb"
    PartOfSpeech.PHRASAL_VERB -> "phr. verb"

    PartOfSpeech.PRONOUN -> "pron"
    PartOfSpeech.DETERMINER -> "det"
    PartOfSpeech.NUMERAL -> "num"
    PartOfSpeech.PREPOSITION -> "prep"
    PartOfSpeech.CONJUNCTION -> "conj"
    PartOfSpeech.ARTICLE -> "art"

    PartOfSpeech.AUXILIARY,
    PartOfSpeech.MODAL -> "aux verb"

    PartOfSpeech.PARTICLE -> "part"
    PartOfSpeech.INTERJECTION -> "intj"
    PartOfSpeech.UNKNOWN -> "other"
}
fun WordDomain.toUi() = {
    UiWord(
        id = id,
        word = lemma,
        whenAdded = whenAdded,
        partOfSpeech = partOfSpeech.toUi()
    )
}