package com.example.vocabanana.core.io.storage

interface FileStorage {
    fun saveText(fileName: String, content: String): String
    fun loadText(filePath: String): String
    fun deleteText(filePath: String): Boolean
}