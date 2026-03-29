package com.example.vocabanana.feature.text.data

import com.example.vocabanana.core.data.ValidateResult
import com.example.vocabanana.feature.text.data.TextConstant.MAX_NAME_LENGTH
import com.example.vocabanana.feature.text.data.TextConstant.NAME_REGEX
import com.example.vocabanana.feature.text.data.TextDomain.Companion.create

@ConsistentCopyVisibility
data class TextDomain private constructor(
    val id: Int,
    val name: String,
    val content: String
) {
    companion object {

        /**
         * Creates a TextDomain object with validation.
         * Use this for any data coming from users or external sources.
         * Returns [ValidateResult.Success] with TextDomain if valid,
         * or [ValidateResult.Error] if validation fails.
         */
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

        /**
         * Creates a TextDomain object without validation.
         * Use this only when you are 100% sure the data is already valid.
         * Faster than [create], but unsafe if the data might be invalid.
         */
        fun unsafeCreate(
            id: Int = 0,
            name: String,
            text: String
        ): TextDomain {
            return TextDomain(
                id = id,
                name = name,
                content = text
            )
        }

        private fun validateText(text: String): ValidateResult<String, TextValidateError> {
            val trimmedText = text.trim()
            if (trimmedText.isEmpty()) return ValidateResult.Error(TextValidateError.EmptyText)
            return ValidateResult.Success(trimmedText)
        }

        private fun validateName(name: String): ValidateResult<String, TextValidateError> {
            val trimmed = name.trim()
            if (trimmed.length > MAX_NAME_LENGTH) return ValidateResult.Error(TextValidateError.TooLongName(MAX_NAME_LENGTH))

            val invalidChar = findFirstInvalidChar(trimmed, NAME_REGEX)
            if (invalidChar != null) return ValidateResult.Error(TextValidateError.InvalidName(invalidChar))

            return ValidateResult.Success(trimmed)
        }

        private fun findFirstInvalidChar(input: String, regex: Regex): Char? {
            return input.firstOrNull { !regex.matches(it.toString()) }
        }
    }
}
