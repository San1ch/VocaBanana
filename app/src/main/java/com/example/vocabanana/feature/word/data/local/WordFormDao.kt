package com.example.vocabanana.feature.word.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface WordFormDao {
    @Query("SELECT * FROM word_form WHERE wordId = :wordId")
    fun getWordFormsByWordId(wordId: Int): List<WordFormsEntity>

    @Query("SELECT * FROM word_form WHERE form = :form")
    fun getWordFormsByForm(form: String): List<WordFormsEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertWordForms(form: List<WordFormsEntity>)
}