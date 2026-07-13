package com.san1ch.vocabanana.core.essentials.repositories

interface FileStorage {
    fun saveText(
        fileName: String,
        content: String,
    ): String

    fun loadText(filePath: String): String

    fun deleteText(filePath: String): Boolean
}
