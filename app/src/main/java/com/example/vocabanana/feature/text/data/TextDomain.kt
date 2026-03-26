package com.example.vocabanana.feature.text.data

import com.example.vocabanana.feature.text.data.TextConstant.MAX_NAME_LENGTH
import com.example.vocabanana.feature.text.data.TextConstant.NAME_REGEX
import com.example.vocabanana.feature.word.data.ValidateResult

@ConsistentCopyVisibility
data class TextDomain private constructor(
    val id: Int,
    val name: String,
    val content: String
) {
    companion object {
        fun create(
            id: Int = 0,
            name: String,
            text: String
        ): ValidateResult<TextDomain, TextValidateError> {
            val validName = when (val validNameResult = validateName(name)) {
                is ValidateResult.Success -> validNameResult.value
                is ValidateResult.Error -> return ValidateResult.Error(validNameResult.error)
            }

            val validText = when (val validTextResult = validateText(text)) {
                is ValidateResult.Success -> validTextResult.value
                is ValidateResult.Error -> return ValidateResult.Error(validTextResult.error)
            }
            return ValidateResult.Success(
                TextDomain(
                    id = id,
                    name = validName,
                    content = validText
                )
            )
        }

        private fun validateText(text: String): ValidateResult<String, TextValidateError> {
            val trimmedText = text.trim()
            if (trimmedText.isEmpty()) return ValidateResult.Error(TextValidateError.EmptyText)

            return ValidateResult.Success(trimmedText)
        }

        private fun validateName(name: String): ValidateResult<String, TextValidateError> {
            val trimmed = name.trim()
            if (trimmed.length > MAX_NAME_LENGTH) return ValidateResult.Error(TextValidateError.TooLongName)

            val invalidChar = findFirstInvalidChar(trimmed, NAME_REGEX)
            if (invalidChar != null) return ValidateResult.Error(
                TextValidateError.InvalidName(
                    invalidChar
                )
            )

            return ValidateResult.Success(trimmed)
        }

        fun findFirstInvalidChar(input: String, regex: Regex): Char? {
            return input.firstOrNull { !regex.matches(it.toString()) }
        }
    }
}

sealed class TextValidateError {
    object EmptyText : TextValidateError()
    object TooLongName : TextValidateError()
    data class InvalidName(val invalidChar: Char) : TextValidateError()
}