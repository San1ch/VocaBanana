package com.san1ch.vocabanana.core.android.database.word

import com.san1ch.vocabanana.core.essentials.model.word.WordDomain
import com.san1ch.vocabanana.core.essentials.model.word.toInt
import com.san1ch.vocabanana.core.essentials.model.word.toPartOfSpeech
import com.san1ch.vocabanana.core.essentials.model.word.toWordState
import com.san1ch.vocabanana.core.android.database.word.local.WordEntity
import com.san1ch.vocabanana.core.android.database.word.local.WordWithForms


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
    definition = word.definition,
)

