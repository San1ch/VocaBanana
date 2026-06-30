package com.san1ch.vocabanana.core.essentials.database.resources.featureproviders

import com.san1ch.vocabanana.core.essentials.database.resources.StringProvider

interface WordStringProvider : StringProvider {
    val wordEmpty: String
    fun wordTooLong(maxLength: Int): String
    fun wordInvalidLemmaChar(invalidChar: Char): String
}