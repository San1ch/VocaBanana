package com.example.vocabanana.feature.vocabulary.presentation

import androidx.lifecycle.viewModelScope
import com.example.vocabanana.core.presentation.BaseViewModel
import com.example.vocabanana.core.presentation.asUiState
import com.example.vocabanana.core.presentation.uistate.UiState
import com.example.vocabanana.feature.database.word.repository.WordRepository
import com.example.vocabanana.feature.word.domain.model.PartOfSpeech
import com.example.vocabanana.feature.word.domain.model.WordDomain
import com.example.vocabanana.feature.word.domain.model.WordFormDomain
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
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

    fun deleteAll() {
        viewModelScope.launch(Dispatchers.IO) {
            wordRepository.deleteAll()
        }
    }

}


data class UiWord(
    val id: Int,
    val word: String,
    val whenAdded: Long,
    val partOfSpeech: String,
    val forms: List<UiWordForm>
)

data class UiWordForm(
    val form: String,
    val partOfSpeech: String
)

fun PartOfSpeech.toUi(): String = when (this) {
    PartOfSpeech.NOUN -> "noun"
    PartOfSpeech.VERB -> "verb"
    PartOfSpeech.ADJECTIVE -> "adj"
    PartOfSpeech.ADVERB -> "adverb"

    PartOfSpeech.PRONOUN -> "pron"
    PartOfSpeech.DETERMINER -> "det"
    PartOfSpeech.NUMERAL -> "num"
    PartOfSpeech.PREPOSITION -> "prep"
    PartOfSpeech.CONJUNCTION -> "conj"
    PartOfSpeech.ARTICLE -> "art"

    PartOfSpeech.AUXILIARY,
    PartOfSpeech.MODAL -> "aux verb"

    PartOfSpeech.PARTICLE -> "part"
    PartOfSpeech.INTERJECTION -> "interjection"
    PartOfSpeech.UNKNOWN -> "other"
}
fun WordDomain.toUi() = UiWord(
    id = id,
    word = lemma,
    whenAdded = whenAdded,
    partOfSpeech = partOfSpeech.toUi(),
    forms = forms.map { it.toUi() }
)

fun WordFormDomain.toUi() = UiWordForm(
    form = form,
    partOfSpeech = partOfSpeech.toUi()
)