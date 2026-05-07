package com.example.vocabanana.feature.ai.texttotext.groq

import com.example.vocabanana.core.utilities.logs.Logger
import com.example.vocabanana.feature.ai.texttotext.TextToTextAi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GroqTextToTextAi @Inject constructor(
    private val logger: Logger
): TextToTextAi {

    private val apiKey = ""
    private val baseUrl = "https://api.groq.com/openai/"

    private val api: LlamaApiService by lazy {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader("Authorization", "Bearer $apiKey")
                    .addHeader("Content-Type", "application/json")
                    .build()
                chain.proceed(request)
            }
            .addInterceptor(logging)
            .build()

        Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(LlamaApiService::class.java)
    }
    //TODO understand how to work with retrofit
    override suspend fun sendRequest(prompt: String): AiResult {
        return try {
            val request = ChatRequest(messages = listOf(ChatMessage("user", prompt)))
            val response = api.getCompletion(request)

            val content = response.choices.firstOrNull()?.message?.content
            if (content != null) {
                AiResult.Success(content)
            } else {
                AiResult.Error.Unknown("Empty response body")
            }
        } catch (e: retrofit2.HttpException) {
            val errorResult = mapHttpError(e.code(), e.message())
            logger.e(e, "HTTP Error: ${e.code()}")
            errorResult
        } catch (e: java.io.IOException) {
            logger.e(e, "Network connection failed")
            AiResult.Error.NetworkError
        } catch (e: Exception) {
            logger.e(e, "Unexpected error")
            AiResult.Error.Unknown(e.localizedMessage ?: "Unknown")
        }
    }

    private fun mapHttpError(code: Int, message: String): AiResult.Error {
        return when (code) {
            401 -> AiResult.Error.InvalidApiKey
            429 -> AiResult.Error.RateLimitExceeded
            in 500..599 -> AiResult.Error.ServerError
            else -> AiResult.Error.Unknown("HTTP $code: $message")
        }
    }
}


data class ChatRequest(
    val model: String = "llama-3.1-8b-instant",
    val messages: List<ChatMessage>,
    val temperature: Double = 0.1
)

data class ChatMessage(
    val role: String,
    val content: String
)

data class ChatResponse(
    val choices: List<ChatChoice>
)

data class ChatChoice(
    val message: ChatMessage
)

interface LlamaApiService {
    @POST("v1/chat/completions")
    suspend fun getCompletion(@Body request: ChatRequest): ChatResponse
}



sealed class AiResult {
    data class Success(val data: String) : AiResult()
    sealed class Error : AiResult() {
        object NetworkError : Error()
        object InvalidApiKey : Error()
        object RateLimitExceeded : Error()
        object ServerError : Error()
        data class Unknown(val message: String) : Error()
    }
}