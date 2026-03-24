package com.example.vocabanana.feature.lemmatization.data

data class GeminiRequest(
    val contents: List<Content>
)

data class LemmaResponse(
    val words: List<LemmaEntry>
)
data class LemmaEntry(
    val original: String,
    val lemma: String
)



data class GeminiResponse(
    val candidates: List<Candidate>? = null,
    val usageMetadata: UsageMetadata? = null
)

data class Candidate(
    val content: Content? = null,
    val finishReason: String? = null
)

data class Content(
    val parts: List<Part>? = null,
    val role: String? = null
)

data class Part(
    val text: String? = null
)

data class UsageMetadata(
    val totalTokenCount: Int = 0
)