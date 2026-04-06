package com.example.vocabanana.core.domain

import com.example.vocabanana.feature.ai.texttotext.groq.AiResult

interface TextToTextAiHub {
    suspend fun sendRequest(prompt: String): AiResult
}