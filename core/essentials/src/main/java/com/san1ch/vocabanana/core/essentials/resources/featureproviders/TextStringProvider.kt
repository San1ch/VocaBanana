package com.san1ch.vocabanana.core.essentials.resources.featureproviders

import com.san1ch.vocabanana.core.essentials.resources.StringProvider

interface TextStringProvider : StringProvider {
    val textValidateEmptyText: String
    val textValidateEmptyName: String

    fun textNameValidateTooLong(maxLength: Int): String

    fun textNameValidateHasInvalidChar(invalidChar: Char): String

    val textValidateNameAlreadyExists: String

    // GenerateWordsFromText loading messages
    val generateWordsPreparingText: String
    val generateWordsAnalyzingLexicon: String
    val generateWordsSavingWords: String

    // GenerateWordsFromText error messages
    val generateWordsNetworkError: String
    val generateWordsInvalidApiKey: String
    val generateWordsRateLimitExceeded: String
    val generateWordsServerError: String
    val generateWordsTextNotFound: String

    fun generateWordsNotAllNewWordsAdded(
        added: Int,
        total: Int,
    ): String

    fun generateWordsUnknownError(message: String): String

    val generateWordsSuccess: String
    val generateWordsAllExists: String
}
