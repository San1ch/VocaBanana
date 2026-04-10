package com.example.vocabanana.feature.database.language.lemmatiazation

import androidx.room.Dao
import androidx.room.Query

@Dao
interface LemmaDao {
    @Query("SELECT DISTINCT lemma FROM `lemmatization-en` WHERE word IN (:words)")
    suspend fun getLemmasForWords(words: List<String>): List<String>
    @Query("SELECT lemma FROM `lemmatization-en` WHERE word = :word")
    suspend fun getLemmaForWord(word: String): String?
    @Query("SELECT word, lemma FROM `lemmatization-en` WHERE word IN (:words)")
    suspend fun getWordLemmaPairs(words: List<String>): List<WordLemmaDto>
    @Query("SELECT DISTINCT lemma FROM `lemmatization-en` WHERE lemma IN (:words)")
    suspend fun findExistingLemmas(words: List<String>): List<String>
    @Query("SELECT DISTINCT word FROM `lemmatization-en` WHERE word IN (:words)")
    suspend fun findExistingWords(words: List<String>): List<String>
}

data class WordLemmaDto(
    val word: String,
    val lemma: String
)