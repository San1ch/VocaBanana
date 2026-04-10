package com.example.vocabanana.feature.database.language.lexicon

import androidx.room.Dao
import androidx.room.Query

@Dao
interface LexiconDao {

    @Query("SELECT word FROM 'lexicon-en' WHERE word IN (:words)")
    suspend fun getExistingWords(words: List<String>): List<String>


}
