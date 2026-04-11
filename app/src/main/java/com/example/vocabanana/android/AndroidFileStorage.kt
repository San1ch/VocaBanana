package com.example.vocabanana.android

import android.content.Context
import com.example.vocabanana.core.io.storage.FileStorage
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject

@Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class AndroidFileStorage @Inject constructor(
    @param:ApplicationContext private val context: Context
) : FileStorage {

    override fun loadText(filePath: String): String {
        return File(filePath).readText()
    }

    override fun deleteText(filePath: String) = File(filePath).delete()

    override fun saveText(fileName: String, content: String): String {
        val safeName = fileName.replace(Regex("[^a-zA-Z0-9]"), "_")
        val file = File(context.filesDir, "texts/$safeName.txt")

        if (!file.parentFile.exists()) file.parentFile.mkdirs()

        file.writeText(content)
        return file.absolutePath
    }
}