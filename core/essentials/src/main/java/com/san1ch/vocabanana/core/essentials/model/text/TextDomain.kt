package com.san1ch.vocabanana.core.essentials.model.text

import com.san1ch.vocabanana.core.essentials.model.ValidateResult
import com.san1ch.vocabanana.core.essentials.model.constants.TextConstant
import com.san1ch.vocabanana.core.essentials.model.text.exception.TextValidateEmptyNameException
import com.san1ch.vocabanana.core.essentials.model.text.exception.TextValidateEmptyTextException
import com.san1ch.vocabanana.core.essentials.model.text.exception.TextValidateNameHasInvalidCharException
import com.san1ch.vocabanana.core.essentials.model.text.exception.TextValidateTooLongException

/**
 * Represents the metadata of a text.
 * This class contains only descriptive information, excluding the heavy content.
 * Ideal for lists, previews, and UI states where performance is key.
 */
data class TextInfo(
    val id: Int,
    val name: String,
    val lastScrollPosition: Float,
    val lastReadTime: Long,
)

/**
 * The full Domain Model for a text entity.
 * Uses composition to separate metadata ([TextInfo]) from the actual body ([content]).
 */
@ConsistentCopyVisibility
data class TextDomain private constructor(
    val info: TextInfo,
    val content: String,
) {
    // Helper properties for easy access to nested metadata
    val id: Int get() = info.id
    val name: String get() = info.name

    companion object {
        /**
         * Creates a TextDomain object with full validation of name and content.
         * Use this for user input or any data that needs to be verified against business rules.
         * Returns [ValidateResult.Success] if valid, or [ValidateResult.Error] with the failure reason.
         */
        fun create(
            id: Int = 0,
            name: String,
            text: String,
            lastScrollPosition: Float,
            lastReadTime: Long,
        ): ValidateResult<TextDomain> {
            // Validate the name against length and character rules
            val validName =
                when (val nameResult = validateName(name)) {
                    is ValidateResult.Success -> nameResult.value
                    is ValidateResult.Error -> return ValidateResult.Error(nameResult.error)
                }

            // Validate the text content (e.g., check if it's not empty)
            val validText =
                when (val textResult = validateText(text)) {
                    is ValidateResult.Success -> textResult.value
                    is ValidateResult.Error -> return ValidateResult.Error(textResult.error)
                }

            return ValidateResult.Success(
                TextDomain(
                    info =
                    TextInfo(
                        id = id,
                        name = validName,
                        lastScrollPosition = lastScrollPosition,
                        lastReadTime = lastReadTime,
                    ),
                    content = validText,
                ),
            )
        }

        /**
         * Creates a TextDomain object skipping all validation checks.
         * Use this only for trusted data (e.g., data already stored in the database).
         */
        fun unsafeCreate(
            id: Int = 0,
            name: String,
            content: String,
            lastScrollPosition: Float,
            lastReadTime: Long,
        ): TextDomain = TextDomain(
            info =
            TextInfo(
                id = id,
                name = name,
                lastScrollPosition = lastScrollPosition,
                lastReadTime = lastReadTime,
            ),
            content = content,
        )

        /**
         * Ensures the text is not empty or just whitespace.
         */
        private fun validateText(text: String): ValidateResult<String> {
            val trimmedText = text.trim()
            if (trimmedText.isEmpty()) return ValidateResult.Error((TextValidateEmptyTextException()))
            return ValidateResult.Success(trimmedText)
        }

        /**
         * Validates the name length and checks for forbidden characters.
         */
        private fun validateName(name: String): ValidateResult<String> {
            val trimmed = name.trim()

            if (trimmed.isEmpty()) return ValidateResult.Error(TextValidateEmptyNameException())

            // Length check
            if (trimmed.length > TextConstant.MAX_NAME_LENGTH) {
                return ValidateResult.Error(TextValidateTooLongException(TextConstant.MAX_NAME_LENGTH))
            }

            // Regex character check
            val invalidChar = findFirstInvalidChar(trimmed, TextConstant.NAME_REGEX)
            if (invalidChar != null) {
                return ValidateResult.Error(TextValidateNameHasInvalidCharException(invalidChar))
            }

            return ValidateResult.Success(trimmed)
        }

        private fun findFirstInvalidChar(
            input: String,
            regex: Regex,
        ): Char? = input.firstOrNull { !regex.matches(it.toString()) }
    }
}
