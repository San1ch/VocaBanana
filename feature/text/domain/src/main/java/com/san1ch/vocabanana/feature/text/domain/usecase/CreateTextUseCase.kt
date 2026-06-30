package com.san1ch.vocabanana.feature.text.domain.usecase

import com.san1ch.vocabanana.core.essentials.database.exceptions.AppException
import com.san1ch.vocabanana.core.essentials.database.model.ValidateResult
import com.san1ch.vocabanana.core.essentials.database.model.text.TextDomain
import com.san1ch.vocabanana.core.essentials.database.model.text.exception.TextValidateNameAlreadyExistsException
import com.san1ch.vocabanana.core.essentials.database.repositories.TextRepository
import javax.inject.Inject


@Suppress("UNCHECKED_CAST")
class CreateTextUseCase @Inject constructor(
    private val textRepository: TextRepository
) {
    operator fun invoke(
        textName: String,
        content: String
    ): Result<Unit> {
        if (!textRepository.isTextNameUnique(textName)) {
            return Result.failure(TextValidateNameAlreadyExistsException())
        }
        return when (val result = TextDomain.create(
            name = textName,
            text = content,
            lastScrollPosition = 0f,
            lastReadTime = 0L
        )) {
            is ValidateResult.Error -> Result.failure(result.error)
            is ValidateResult.Success -> {
                textRepository.saveText(result.value)
                Result.success(Unit)
            }
        }
    }
}