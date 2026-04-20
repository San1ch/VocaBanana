package com.example.vocabanana.core.word.mapper

import com.example.vocabanana.R
import com.example.vocabanana.core.presentation.UiText
import com.example.vocabanana.core.word.domain.model.WordDomain
import com.example.vocabanana.core.word.domain.model.WordValidateError
import com.example.vocabanana.core.word.domain.model.toInt
import com.example.vocabanana.core.word.domain.model.toPartOfSpeech
import com.example.vocabanana.core.word.domain.model.toWordState
import com.example.vocabanana.feature.database.word.local.WordEntity
import com.example.vocabanana.feature.database.word.local.WordWithForms


// WordMapper.kt
fun WordDomain.toWordEntity() = WordEntity(
    id = id,
    lemma = lemma,
    countInTheTexts = countInTheTexts,
    state = state.toInt(),
    whenAdded = whenAdded,
    partOfSpeech = partOfSpeech.toInt(),
    definition = definition
)

fun WordWithForms.toDomain() = WordDomain.createUnsafe(
    id = word.id,
    lemma = word.lemma,
    whenAdded = word.whenAdded,
    state = word.state.toWordState(),
    partOfSpeech = word.partOfSpeech.toPartOfSpeech(),
    forms = forms.map { it.form },
    definition = word.definition,
    countInTheTexts = word.countInTheTexts
)

fun WordValidateError.toUiText() = when (this) {
    WordValidateError.Empty -> UiText.StringResource(R.string.word_validate_error_empty)
    is WordValidateError.TooLong -> UiText.StringResource(R.string.word_validate_error_length, maxLength)
    is WordValidateError.InvalidLemmaChar -> UiText.StringResource(R.string.word_validate_error_invalid_lemma_char, char)
}