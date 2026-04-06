package com.example.vocabanana.feature.ai.texttotext

import com.example.vocabanana.feature.ai.texttotext.groq.AiResult

interface TextToTextAi {
    suspend fun sendRequest(prompt: String): AiResult
}