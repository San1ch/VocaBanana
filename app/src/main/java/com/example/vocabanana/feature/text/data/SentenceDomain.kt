package com.example.vocabanana.feature.text.data

import com.example.vocabanana.feature.word.data.WordDomain

data class SentenceDomain(
    var id: Int,
    var text: String,
    val words: List<WordDomain>
)