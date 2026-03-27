package com.example.vocabanana.feature.text.domain.usecase

import com.example.vocabanana.core.data.ValidateResult
import com.example.vocabanana.feature.text.data.TextDomain
import com.example.vocabanana.feature.text.data.TextValidateError
import com.example.vocabanana.feature.text.domain.TextRepository
import javax.inject.Inject


class AddTextUseCase @Inject constructor(
    private val textRepository: TextRepository
) {
    operator fun invoke(
        textName: String,
        content: String
    ): TextValidateError?{
        return when (val result = TextDomain.create(name = textName, text = content)) {
            is ValidateResult.Error -> result.error
            is ValidateResult.Success -> {
                textRepository.insertText(result.value)
                null
            }
        }
    }
}