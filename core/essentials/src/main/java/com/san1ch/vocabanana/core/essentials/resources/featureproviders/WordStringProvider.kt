package com.san1ch.vocabanana.core.essentials.resources.featureproviders

import com.san1ch.vocabanana.core.essentials.resources.StringProvider

interface WordStringProvider : StringProvider {
    val wordEmpty: String

    fun wordTooLong(maxLength: Int): String

    fun wordInvalidLemmaChar(invalidChar: Char): String
}
