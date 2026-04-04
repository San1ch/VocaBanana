package com.example.vocabanana.core.ai

import com.example.vocabanana.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AiCenter @Inject constructor() {

    private val apiKey = BuildConfig.GROQ_API_KEY
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

    suspend fun sendRequest(systemPrompt: String, userMessage: String): String {
        return try {
            val request = ChatRequest(
                messages = listOf(
                    ChatMessage(role = "system", content = systemPrompt),
                    ChatMessage(role = "user", content = userMessage)
                )
            )

            val response = api.getCompletion(request)
            response.choices.firstOrNull()?.message?.content ?: "Error: Empty response"
        } catch (e: Exception) {
            "Error: ${e.localizedMessage}"
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