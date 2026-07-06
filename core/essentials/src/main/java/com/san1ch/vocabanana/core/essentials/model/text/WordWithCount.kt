package com.san1ch.vocabanana.core.essentials.model.text

import com.san1ch.vocabanana.core.essentials.model.word.WordDomain

data class WordWithCount(
    val word: WordDomain,
    val count: Int
){
    val state = word.state
}