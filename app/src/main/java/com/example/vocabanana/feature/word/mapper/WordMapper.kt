package com.example.vocabanana.feature.word.mapper

import com.example.vocabanana.feature.database.word.local.WordEntity
import com.example.vocabanana.feature.word.domain.model.PartOfSpeech
import com.example.vocabanana.feature.word.domain.model.WordDomain
import com.example.vocabanana.feature.word.domain.model.toInt
import com.example.vocabanana.feature.word.domain.model.toPartOfSpeech
import com.example.vocabanana.feature.word.domain.model.toWordState


fun WordDomain.toWordEntity() = WordEntity(
    id = id,
    lemma = lemma,
    state = state.toInt(),
    whenAdded = whenAdded,
    partOfSpeech = partOfSpeech.toInt(),
    forms = forms
)
fun WordEntity.toDomain() = WordDomain.createUnsafe(
    id = id,
    lemma = lemma,
    whenAdded = whenAdded,
    state = state.toWordState(),
    partOfSpeech = partOfSpeech.toPartOfSpeech(),
    forms = forms
)

