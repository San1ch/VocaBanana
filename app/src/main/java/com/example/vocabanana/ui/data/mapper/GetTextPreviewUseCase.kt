package com.example.vocabanana.ui.data.mapper

import com.example.vocabanana.feature.text.data.TextDomain
import com.example.vocabanana.feature.text.domain.TextRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTextsUseCase @Inject constructor(
    private val repository: TextRepository
) {
    operator fun invoke(): Flow<List<TextDomain>> {
        return repository.getTexts()
    }
}