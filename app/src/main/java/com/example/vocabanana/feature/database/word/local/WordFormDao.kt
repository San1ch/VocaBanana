package com.example.vocabanana.feature.database.word.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface WordFormDao {
    @Query("SELECT * FROM word_form WHERE wordId = :wordId")
    fun getWordFormsByWordId(wordId: Int): List<WordFormEntity>

    @Query("SELECT form FROM word_form")
    fun getAlLForms(): List<String>


    @Query("SELECT * FROM word_form WHERE form = :form")
    fun formExists(form: String): Boolean
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertWordForms(form: List<WordFormEntity>)

    @Query("SELECT form FROM word_form WHERE form IN (:forms)")
    fun getExistingForms(forms: List<String>): List<String>
}

