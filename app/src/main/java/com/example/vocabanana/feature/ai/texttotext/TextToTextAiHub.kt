package com.example.vocabanana.feature.ai.texttotext

import com.example.vocabanana.core.domain.TextToTextAiHub
import com.example.vocabanana.core.utilities.logs.Logger
import com.example.vocabanana.feature.ai.texttotext.groq.AiResult
import kotlinx.coroutines.delay
import javax.inject.Inject

class TextToTextAiHubImpl @Inject constructor(
    private val ai: TextToTextAi,
    private val logger: Logger
) : TextToTextAiHub {
    override suspend fun sendRequest(prompt: String): AiResult {
        var delayMs = 5000L

        repeat(5) {
            when (val result = ai.sendRequest(prompt)) {
                is AiResult.Error.RateLimitExceeded -> {
                    delay(delayMs)
                    delayMs = (delayMs * 2).coerceAtMost(60000L)
                    logger.d("Rate limit exceeded, waiting for $delayMs ms", tag = "TextToTextAiHubImpl")
                }

                else -> return result
            }
        }
        return AiResult.Error.RateLimitExceeded
    }
}