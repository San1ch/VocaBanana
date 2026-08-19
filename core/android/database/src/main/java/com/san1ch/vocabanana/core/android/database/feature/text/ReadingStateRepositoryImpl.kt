package com.san1ch.vocabanana.core.android.database.feature.text

import com.san1ch.vocabanana.core.essentials.DataChangeTracker
import com.san1ch.vocabanana.core.essentials.model.TextAppearanceSettings
import com.san1ch.vocabanana.core.essentials.model.word.WordState
import com.san1ch.vocabanana.feature.text.domain.ReadingStateRepository
import com.san1ch.vocabanana.feature.text.domain.model.ReadingState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ReadingStateRepositoryImpl @Inject constructor(
    private val readingStateDao: ReadingStateDao,
    private val dataChangeTracker: DataChangeTracker,
) : ReadingStateRepository {

    override fun getReadingStateByIdFlow(id: Int): Flow<ReadingState> = readingStateDao.getReadingStateByIdFlow(id).map { entity ->
        entity?.toDomain() ?: ReadingState(id)
    }

    override val getAllReadingStateFlow: Flow<List<ReadingState>>
        get() = readingStateDao.getAllReadingStateFlow().map { list ->
            list.map { it.toDomain() }
        }

    override suspend fun updateProgress(
        textId: Int,
        lastReadTime: Long,
        scrollPosition: Float,
    ) {
        val currentState = readingStateDao.getReadingStateById(textId) ?: ReadingState(textId).toEntity()
        val domainState = currentState.toDomain().copy(
            lastReadTime = lastReadTime,
            lastScrollPosition = scrollPosition,
        )
        readingStateDao.insertReadingState(domainState.toEntity())
    }

    override suspend fun updateAppearance(
        textId: Int,
        appearance: TextAppearanceSettings,
    ) {
        val currentState = readingStateDao.getReadingStateById(textId) ?: ReadingState(textId).toEntity()
        val domainState = currentState.toDomain().copy(
            fontSize = appearance.fontSize,
            lineSpacing = appearance.lineSpacing,
            paragraphSpacing = appearance.paragraphSpacing,
            horizontalPadding = appearance.horizontalPadding,
        )
        readingStateDao.insertReadingState(domainState.toEntity())
        dataChangeTracker.notifyDataChanged()
    }

    override suspend fun updateWordStates(
        textId: Int,
        wordStates: Set<WordState>,
    ) {
        val currentState = readingStateDao.getReadingStateById(textId) ?: ReadingState(textId).toEntity()
        val domainState = currentState.toDomain().copy(
            activeWordStates = wordStates,
        )
        readingStateDao.insertReadingState(domainState.toEntity())
        dataChangeTracker.notifyDataChanged()
    }
}
