package com.san1ch.vocabanana.core.essentials

import kotlinx.coroutines.flow.Flow

interface IsCloudBackupEnabledUseCase {
    operator fun invoke(): Flow<Boolean>
}
