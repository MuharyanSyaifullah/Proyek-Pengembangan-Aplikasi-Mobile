package id.pusakakata.data.repository

import id.pusakakata.data.local.PusakaDatabase
import id.pusakakata.data.remote.ApiService
import id.pusakakata.domain.model.Word
import id.pusakakata.domain.repository.ItemRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.datetime.Clock

class ItemRepositoryImpl(
    db: PusakaDatabase,
    private val apiService: ApiService
) : ItemRepository {
    private val queries = db.pusakaDatabaseQueries

    override fun getAllWords(): Flow<List<Word>> {
        return queries.getAllWords().asFlow().mapToList(Dispatchers.IO).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getWordById(id: String): Word? {
        return queries.getWordById(id).executeAsOneOrNull()?.toDomain()
    }

    override suspend fun insertWord(word: Word) {
        queries.insertWord(
            id = word.id,
            term = word.term,
            definition = word.definition,
            category = word.category,
            createdAt = Clock.System.now().toEpochMilliseconds(),
            intervalDays = word.srsData.intervalDays.toLong(),
            easeFactor = word.srsData.easeFactor,
            nextReview = word.srsData.nextReview?.toEpochMilliseconds(),
            level = word.srsData.level.toLong()
        )
    }

    override suspend fun updateWord(word: Word) {
        queries.updateWord(
            term = word.term,
            definition = word.definition,
            category = word.category,
            intervalDays = word.srsData.intervalDays.toLong(),
            easeFactor = word.srsData.easeFactor,
            nextReview = word.srsData.nextReview?.toEpochMilliseconds(),
            level = word.srsData.level.toLong(),
            id = word.id
        )
    }

    override suspend fun deleteWord(id: String) {
        queries.deleteWord(id)
    }

    override suspend fun searchOnline(word: String): Result<Word> {
        return try {
            val response = apiService.fetchDefinition(word)
            if (response.status && response.data != null) {
                Result.success(
                    Word(
                        id = "", // ID will be generated on save
                        term = response.data.lema,
                        definition = response.data.arti.joinToString("\n"),
                        category = "Umum"
                    )
                )
            } else {
                val errorMsg = when (response.message?.lowercase()) {
                    "illegal input status" -> "Kata tidak ditemukan atau input tidak valid."
                    null -> "Data tidak ditemukan."
                    else -> response.message
                }
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            // Handle serialization error or network error
            Result.failure(Exception("Gagal mengambil data. Pastikan koneksi internet aktif."))
        }
    }
}
