package com.san1ch.vocabanana.core.android.commonandroid.filestorage

import android.content.Context
import com.san1ch.vocabanana.core.essentials.repositories.FileStorage
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject

class AndroidFileStorage @Inject constructor(
    @param:ApplicationContext private val context: Context,
) : FileStorage {

    override fun loadText(filePath: String): String = File(filePath).readText()

    override fun deleteText(filePath: String) = File(filePath).delete()

    override fun saveText(fileName: String, content: String): String {
        val safeName = fileName.replace(Regex("[^a-zA-Z0-9]"), "_")
        val file = File(context.filesDir, "texts/$safeName.txt")

        file.parentFile?.let { parent ->
            if (!parent.exists()) {
                parent.mkdirs()
            }
        }

        file.writeText(content)
        return file.absolutePath
    }
}
