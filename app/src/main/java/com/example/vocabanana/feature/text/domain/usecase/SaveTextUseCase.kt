package com.example.vocabanana.feature.text.domain.usecase

import com.example.vocabanana.feature.text.domain.TextRepository
import javax.inject.Inject


class SaveTextUseCase @Inject constructor(
    textRepository: TextRepository
) {
    suspend operator fun invoke(textName: String, content: String) {

    }
}