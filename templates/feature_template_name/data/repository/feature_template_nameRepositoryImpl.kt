package com.example.vocabanana.feature.<low_feature>.data.repository

import com.example.vocabanana.feature.<low_feature>.data.local.<feature>Dao
import com.example.vocabanana.feature.<low_feature>.domain.<feature>Repository
import javax.inject.Inject

class <feature>RepositoryImpl @Inject constructor(private val dao: <feature>Dao) :
    <feature>Repository {
}