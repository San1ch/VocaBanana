package com.san1ch.vocabanana.core.android.database.feature.text

import com.san1ch.vocabanana.feature.text.domain.ReadingStateRepository
import com.san1ch.vocabanana.feature.text.domain.model.ReadingState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ReadingStateRepositoryImpl @Inject constructor(
    private val readingStateDao: ReadingStateDao
) : ReadingStateRepository {
    override fun getReadingStateByIdFlow(id: Int): Flow<ReadingState> {
        return readingStateDao.getReadingStateByIdFlow(id).map { entity ->
            entity?.toDomain() ?: ReadingState(id)
        }
    }

    override val getAllReadingStateFlow: Flow<List<ReadingState>> get() = readingStateDao.getAllReadingStateFlow().map { list ->
        list.map { it.toDomain() }
    }

    override suspend fun setReadingState(readingState: ReadingState) {
        readingStateDao.insertReadingState(readingState.toEntity())
    }

    override suspend fun updateReadingState(
        id: Int,
        transform: (ReadingState) -> ReadingState
    ) {
        val currentState = readingStateDao.getReadingStateById(id) ?: return
        val newState = transform(currentState.toDomain())
        readingStateDao.insertReadingState(newState.toEntity())
    }


}