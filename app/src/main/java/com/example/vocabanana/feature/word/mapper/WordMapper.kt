package com.example.vocabanana.feature.word.mapper

import com.example.vocabanana.feature.database.word.local.WordEntity
import com.example.vocabanana.feature.database.word.local.WordFormEntity
import com.example.vocabanana.feature.database.word.local.WordWithFormsEntity
import com.example.vocabanana.feature.database.word.toInt
import com.example.vocabanana.feature.database.word.toWordState
import com.example.vocabanana.feature.word.domain.model.PartOfSpeech
import com.example.vocabanana.feature.word.domain.model.WordDomain
import com.example.vocabanana.feature.word.domain.model.WordFormDomain
import com.example.vocabanana.feature.word.domain.model.toInt


fun WordDomain.toWordEntity() = WordEntity(
    id = id,
    lemma = lemma,
    state = state.toInt(),
    whenAdded = whenAdded,
    partOfSpeech = partOfSpeech.toInt(),
)
fun WordWithFormsEntity.toDomain() = WordDomain.createUnsafe(
    id = word.id,
    lemma = word.lemma,
    whenAdded = word.whenAdded,
    state = word.state.toWordState(),
    partOfSpeech = PartOfSpeech.entries[word.partOfSpeech],
    forms = forms.map { it.toDomain(word.id) }
)


fun WordFormDomain.toEntity(wordId: Int) = WordFormEntity(
    id = id,
    wordId = wordId,
    form = form,
    partOfSpeech = partOfSpeech.toInt()
)

fun WordFormEntity.toDomain(wordId: Int) = WordFormDomain.createUnsafe(
    id = id,
    wordId = wordId,
    form = form,
    partOfSpeech = PartOfSpeech.entries[partOfSpeech]
)