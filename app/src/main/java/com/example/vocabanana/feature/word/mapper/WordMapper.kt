package com.example.vocabanana.feature.word.mapper

import com.example.vocabanana.feature.database.word.local.WordEntity
import com.example.vocabanana.feature.database.word.local.WordWithForms
import com.example.vocabanana.feature.word.domain.model.WordDomain
import com.example.vocabanana.feature.word.domain.model.toInt
import com.example.vocabanana.feature.word.domain.model.toPartOfSpeech
import com.example.vocabanana.feature.word.domain.model.toWordState


// WordMapper.kt
fun WordDomain.toWordEntity() = WordEntity(
    id = id,
    lemma = lemma,
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
    definition = word.definition
)