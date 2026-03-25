package com.example.vocabanana.feature.text.data

object TextConstant {

    const val MAX_NAME_LENGTH = 100
    val NAME_REGEX = Regex("^[\\p{L}\\d\\s\\-]+$")
    val TEXT_REGEX = Regex("^[\\p{L}\\d\\s.,!?:;\"'()\\-\\n\r]+$")

}