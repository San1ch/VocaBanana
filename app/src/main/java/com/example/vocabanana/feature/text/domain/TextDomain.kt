package com.example.vocabanana.feature.text.domain

import com.example.vocabanana.core.data.ValidateResult
import com.example.vocabanana.core.database.text.TextConstant
import com.example.vocabanana.core.database.text.TextValidateError

@ConsistentCopyVisibility
data class TextDomain private constructor(
    val id: Int,
    val name: String,
    val content: String,

    val lastScrollPosition: Float,
    val lastReadTime: Long
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
            text: String,
            lastScrollPosition: Float,
            lastReadTime: Long
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
                    content = validText,
                    lastScrollPosition = lastScrollPosition,
                    lastReadTime = lastReadTime
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
            text: String,
            lastScrollPosition: Float,
            lastReadTime: Long
        ): TextDomain {
            return TextDomain(
                id = id,
                name = name,
                content = text,
                lastScrollPosition = lastScrollPosition,
                lastReadTime = lastReadTime
            )
        }

        private fun validateText(text: String): ValidateResult<String, TextValidateError> {
            val trimmedText = text.trim()
            if (trimmedText.isEmpty()) return ValidateResult.Error(TextValidateError.EmptyText)
            return ValidateResult.Success(trimmedText)
        }

        private fun validateName(name: String): ValidateResult<String, TextValidateError> {
            val trimmed = name.trim()
            if (trimmed.length > TextConstant.MAX_NAME_LENGTH) return ValidateResult.Error(
                TextValidateError.TooLongName(
                    TextConstant.MAX_NAME_LENGTH
                )
            )

            val invalidChar = findFirstInvalidChar(trimmed, TextConstant.NAME_REGEX)
            if (invalidChar != null) return ValidateResult.Error(
                TextValidateError.InvalidName(
                    invalidChar
                )
            )

            return ValidateResult.Success(trimmed)
        }

        private fun findFirstInvalidChar(input: String, regex: Regex): Char? {
            return input.firstOrNull { !regex.matches(it.toString()) }
        }
    }
}