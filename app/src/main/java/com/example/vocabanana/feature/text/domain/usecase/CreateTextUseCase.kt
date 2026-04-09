package com.example.vocabanana.feature.text.domain.usecase

import com.example.vocabanana.core.domain.model.ValidateResult
import com.example.vocabanana.feature.text.domain.model.TextValidateError
import com.example.vocabanana.core.database.TextRepository
import com.example.vocabanana.feature.text.domain.model.TextDomain
import javax.inject.Inject


class CreateTextUseCase @Inject constructor(
    private val textRepository: TextRepository
) {
    operator fun invoke(
        textName: String,
        content: String
    ): TextValidateError? {
        if (!textRepository.isTextNameUnique(textName)) {
            return TextValidateError.NameAlreadyExists
        }
        return when (val result = TextDomain.create(
            name = textName,
            text = content,
            lastScrollPosition = 0f,
            lastReadTime = 0L
        )) {
            is ValidateResult.Error -> result.error
            is ValidateResult.Success -> {
                textRepository.insertText(result.value)
                null
            }
        }
    }
}