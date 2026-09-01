package com.san1ch.vocabanana.feature.main.domain.usecases

import com.san1ch.vocabanana.core.essentials.IsCloudBackupEnabledUseCase
import com.san1ch.vocabanana.core.essentials.model.GoogleAuthState
import com.san1ch.vocabanana.core.essentials.repositories.GoogleRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class IsCloudBackupEnabledUseCaseImpl @Inject constructor(
    private val googleRepository: GoogleRepository,
) : IsCloudBackupEnabledUseCase {
    override fun invoke(): Flow<Boolean> = flow {
        googleRepository.getActiveAccountState().collect {
            emit(it is GoogleAuthState.SignedIn)
        }
    }
}
