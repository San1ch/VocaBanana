package com.example.vocabanana.feature.database.language.lexicon

import androidx.room.Dao
import androidx.room.MapColumn
import androidx.room.Query

@Dao
interface LexiconDao {

    @Query("SELECT word FROM 'lexicon-en' WHERE word IN (:words)")
    suspend fun getExistingWords(words: List<String>): List<String>

    @Query("SELECT word, type FROM 'lexicon-en' WHERE word IN (:words)")
    suspend fun getPartOfSpeeches(words: List<String>):
            Map<
            @MapColumn(columnName = "word") String,
            @MapColumn(columnName = "type") String
                    >
    @Query("SELECT * FROM 'lexicon-en' WHERE word IN (:words)")
    suspend fun getWordsFromWords(words: List<String>): List<LexiconEntity>
}
