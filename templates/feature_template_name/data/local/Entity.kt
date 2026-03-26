package com.example.vocabanana.feature.<low_feature>.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

//TODO: change table name and add entity to AppDatabase
@Entity(tableName = "")
data class <feature>Entity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
)
