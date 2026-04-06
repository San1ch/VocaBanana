package com.example.vocabanana.core.network


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