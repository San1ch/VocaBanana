/* <<<<<<<<<<<<<<  ✨ Windsurf Command 🌟 >>>>>>>>>>>>>>>> */
package com.san1ch.vocabanana.core.android.network.googledrive

import android.content.Context
import com.san1ch.vocabanana.core.essentials.Logger
import com.san1ch.vocabanana.core.essentials.backup.GoogleDriveTokenProvider
import com.san1ch.vocabanana.core.essentials.network.CloudStorageClient
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query
import java.io.File
import javax.inject.Inject

@Serializable
data class GoogleDriveFileListResponse(
    @SerialName(value = "files")
    val files: List<GoogleDriveFileItem>?
)

@Serializable
data class GoogleDriveFileItem(
    @SerialName("id")
    val id: String,

    @SerialName("name")
    val name: String,

    @SerialName("size")
    val size: String? = null
)

class GoogleDriveStorageClient @Inject constructor(
    private val tokenManager: GoogleDriveTokenProvider,
    private val driveApiService: GoogleDriveApiService,
    private val logger: Logger,
    @param:ApplicationContext private val context: Context
) : CloudStorageClient {
    private val fileName = "vocabanana_backup.zip"
    private val systemDriveFolder = "appDataFolder"

    override suspend fun uploadOrUpdateBackup(backupFile: File): Result<Unit> = runCatching {
        logger.d("GoogleDrive", "Starting cloud backup upload...")
        val authHeader = getAuthHeader()

        val searchQuery =
            "name = '$fileName' and '$systemDriveFolder' in parents and trashed = false"
        logger.d("GoogleDrive", "Searching file with query: $searchQuery")

        val searchResult =
            driveApiService.listFiles(authHeader = authHeader, searchQuery = searchQuery)
        logger.d("GoogleDrive", "Search result files count: ${searchResult.files?.size ?: 0}")

        val existingFileId = searchResult.files?.firstOrNull()?.id
        logger.d("GoogleDrive", "Existing file ID: $existingFileId")

        val requestFile = backupFile.asRequestBody("application/zip".toMediaTypeOrNull())
        val filePart = MultipartBody.Part.createFormData("file", backupFile.name, requestFile)

        if (existingFileId != null) {
            logger.d("GoogleDrive", "Updating existing file with ID: $existingFileId")
            val metadataJson = """{"name":"$fileName"}"""
            val metadataPart = MultipartBody.Part.createFormData(
                "metadata", null,
                metadataJson.toRequestBody("application/json".toMediaTypeOrNull())
            )
            driveApiService.updateBackupFile(
                authHeader = authHeader,
                fileId = existingFileId,
                metadata = metadataPart,
                file = filePart
            )
        } else {
            logger.d("GoogleDrive", "Uploading new file...")
            val metadataJson = """{"name":"$fileName", "parents":["$systemDriveFolder"]}"""
            val metadataPart = MultipartBody.Part.createFormData(
                "metadata", null,
                metadataJson.toRequestBody("application/json".toMediaTypeOrNull())
            )
            driveApiService.uploadBackupFile(
                authHeader = authHeader,
                metadata = metadataPart,
                file = filePart
            )
        }
        logger.d("GoogleDrive", "Cloud backup successfully uploaded/updated")
    }.onFailure { e ->
        logger.d("GoogleDrive", "Upload failed with error: ${e.message}")
    }

    override suspend fun downloadBackup(): Result<File> = runCatching {
        logger.d("GoogleDrive", "Starting cloud backup download...")
        val authHeader = getAuthHeader()

        val searchQuery = "name = '$fileName' and trashed = false"
        logger.d("GoogleDrive", "Searching file for download with query: $searchQuery")

        val searchResult =
            driveApiService.listFiles(authHeader = authHeader, searchQuery = searchQuery)
        logger.d("GoogleDrive", "Search result files count: ${searchResult.files?.size ?: 0}")

        val fileId = searchResult.files?.firstOrNull()?.id
            ?: throw IllegalStateException("Cloud backup file not found in appDataFolder").also {
                logger.d("GoogleDrive", "Error: Cloud backup file not found!")
            }

        logger.d("GoogleDrive", "Found file ID to download: $fileId")
        val responseBody = driveApiService.downloadBackupFile(authHeader, fileId)

        val tempFile = File(context.cacheDir, "downloaded_backup.zip")
        logger.d("GoogleDrive", "Writing downloaded bytes to temp file: ${tempFile.absolutePath}")

        responseBody.use { body ->
            tempFile.outputStream().use { outputStream ->
                body.byteStream().copyTo(outputStream)
            }
        }

        logger.d(
            "GoogleDrive",
            "Download completed successfully, file size: ${tempFile.length()} bytes"
        )
        tempFile
    }.onFailure { e ->
        logger.d("GoogleDrive", "Download failed with error: ${e.message}")
    }

    
    override suspend fun deleteBackup() = runCatching {
        logger.d("GoogleDrive", "Starting cloud backup delete...")
        val authHeader = getAuthHeader()

        val searchQuery = "name = '$fileName' and trashed = false"
        logger.d("GoogleDrive", "Searching file to delete with query: $searchQuery")

        val searchResult =
            driveApiService.listFiles(authHeader = authHeader, searchQuery = searchQuery)
        logger.d("GoogleDrive", "Search result files count: ${searchResult.files?.size ?: 0}")

        val fileId = searchResult.files?.firstOrNull()?.id
            ?: throw IllegalStateException("Cloud backup file not found").also {
                logger.d("GoogleDrive", "Error: Cloud backup file not found!")
            }

        logger.d("GoogleDrive", "Found file ID to delete: $fileId")
        driveApiService.deleteBackupFile(authHeader, fileId)
        logger.d("GoogleDrive", "Cloud backup successfully deleted")
    }.onFailure { e ->
        logger.d("GoogleDrive", "Delete failed with error: ${e.message}")
        
    }
    

    private suspend fun getAuthHeader(): String {
        val tokenResult = tokenManager.obtainAccessToken()
        val accessToken = tokenResult.getOrThrow()
        return "Bearer $accessToken"
    }


}

interface GoogleDriveApiService {

    @GET("drive/v3/files")
    suspend fun listFiles(
        @Header("Authorization") authHeader: String,
        @Query("spaces") spaces: String = "appDataFolder",
        @Query("q") searchQuery: String,
        @Query("fields") fields: String = "files(id,name,size)"
    ): GoogleDriveFileListResponse

    @Multipart
    @POST("upload/drive/v3/files")
    suspend fun uploadBackupFile(
        @Header("Authorization") authHeader: String,
        @Query("uploadType") uploadType: String = "multipart",
        @Part metadata: MultipartBody.Part,
        @Part file: MultipartBody.Part
    ): GoogleDriveFileItem

    @Multipart
    @PATCH("upload/drive/v3/files/{fileId}")
    suspend fun updateBackupFile(
        @Header("Authorization") authHeader: String,
        @Path("fileId") fileId: String,
        @Query("uploadType") uploadType: String = "multipart",
        @Part metadata: MultipartBody.Part,
        @Part file: MultipartBody.Part
    ): GoogleDriveFileItem

    @GET("drive/v3/files/{fileId}")
    suspend fun downloadBackupFile(
        @Header("Authorization") authHeader: String,
        @Path("fileId") fileId: String,
        @Query("alt") alt: String = "media"
    ): ResponseBody

    @DELETE("drive/v3/files/{fileId}")
    suspend fun deleteBackupFile(
        @Header("Authorization") authHeader: String,
        @Path("fileId") fileId: String
    )
}
/* <<<<<<<<<<  4dd11545-9e88-41c0-a20e-f0530db9030d  >>>>>>>>>>> */