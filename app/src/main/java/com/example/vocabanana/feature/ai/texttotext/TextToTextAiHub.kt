package com.example.vocabanana.feature.ai.texttotext

import com.example.vocabanana.core.domain.TextToTextAiHub
import com.example.vocabanana.feature.ai.texttotext.TextToTextAi
import com.example.vocabanana.feature.ai.texttotext.groq.AiResult
import javax.inject.Inject

class TextToTextAiHubImpl @Inject constructor(
    private val ai: TextToTextAi
): TextToTextAiHub {
    override suspend fun sendRequest(prompt: String): AiResult {
        val result = ai.sendRequest(prompt)
        return result
    }
}