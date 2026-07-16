package com.san1ch.vocabanana.feature.text.presentation.textlist

import android.content.Context
import com.san1ch.vocabanana.core.essentials.resources.featureproviders.TextStringProvider
import com.san1ch.vocabanana.feature.text.presentation.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class TextStringProviderImpl @Inject constructor(
    @param:ApplicationContext private val context: Context,
) : TextStringProvider {

    override val textValidateEmptyText: String
        get() = context.getString(R.string.validate_empty_text)

    override val textValidateEmptyName: String
        get() = context.getString(R.string.validate_empty_name)

    override fun textNameValidateTooLong(maxLength: Int): String = context.getString(R.string.validate_name_too_long, maxLength)

    override fun textNameValidateHasInvalidChar(invalidChar: Char): String = context.getString(R.string.validate_name_has_invalid_char, invalidChar)

    override val textValidateNameAlreadyExists: String
        get() = context.getString(R.string.validate_name_already_exists)

    // --- GenerateWordsFromText ---

    override val generateWordsNetworkError: String
        get() = context.getString(R.string.gen_words_network_error)

    override val generateWordsInvalidApiKey: String
        get() = context.getString(R.string.gen_words_invalid_api_key)

    override val generateWordsRateLimitExceeded: String
        get() = context.getString(R.string.gen_words_rate_limit_exceeded)

    override val generateWordsServerError: String
        get() = context.getString(R.string.gen_words_server_error)

    override val generateWordsTextNotFound: String
        get() = context.getString(R.string.gen_words_text_not_found)

    override fun generateWordsNotAllNewWordsAdded(added: Int, total: Int): String = context.getString(
        R.string.gen_words_partial, added, total)

    override fun generateWordsUnknownError(message: String): String = context.getString(R.string.gen_words_unknown, message)
    override val generateWordsSuccess: String
        get() = context.getString(R.string.gen_words_success)
    override val generateWordsAllExists: String
        get() = context.getString(R.string.gen_words_all_exists)
    override val generateWordsPreparingText: String
        get() = context.getString(R.string.gen_words_preparing_text)

    override val generateWordsAnalyzingLexicon: String
        get() = context.getString(R.string.gen_words_analyzing_lexicon)

    override val generateWordsSavingWords: String
        get() = context.getString(R.string.gen_words_saving_words)
}
