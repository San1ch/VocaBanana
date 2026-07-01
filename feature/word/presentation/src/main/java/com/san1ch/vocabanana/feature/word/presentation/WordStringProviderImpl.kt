package com.san1ch.vocabanana.feature.word.presentation

import android.content.Context
import com.san1ch.vocabanana.core.essentials.resources.featureproviders.WordStringProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
class WordStringProviderImpl @Inject constructor(
    @param:ApplicationContext private val context: Context
): WordStringProvider {
    override val wordEmpty: String = context.getString(R.string.word_empty)
    override fun wordTooLong(maxLength: Int): String = context.getString(R.string.word_too_long, maxLength)
    override fun wordInvalidLemmaChar(invalidChar: Char): String = context.getString(R.string.word_invalid_lemma_char, invalidChar)
}
