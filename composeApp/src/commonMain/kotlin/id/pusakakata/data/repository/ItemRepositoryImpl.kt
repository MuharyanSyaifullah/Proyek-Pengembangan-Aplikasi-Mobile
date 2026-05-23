package id.pusakakata.data.repository

import id.pusakakata.data.local.PusakaDatabase
import id.pusakakata.data.remote.ApiService
import id.pusakakata.data.remote.GeminiService
import id.pusakakata.domain.model.Word
import id.pusakakata.domain.repository.ItemRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOne
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.datetime.Clock
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class ItemRepositoryImpl(
    private val db: PusakaDatabase,
    private val apiService: ApiService,
    private val geminiService: GeminiService
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

    @OptIn(ExperimentalUuidApi::class)
    override suspend fun searchAndSave(word: String): Result<Word> {
        return try {
            // Coba Kamus Resmi dulu
            val response = try { 
                apiService.fetchDefinition(word) 
            } catch (e: Exception) { 
                null 
            }
            
            val definition = if (response != null && response.status && response.data != null) {
                response.data.arti.joinToString("\n")
            } else {
                // Jika tidak ada di kamus atau API error, Tanya AI
                geminiService.generateDefinition(word)
            }

            val newWord = Word(
                id = Uuid.random().toString(),
                term = response?.data?.lema ?: word.replaceFirstChar { it.uppercase() },
                definition = definition,
                category = if (response?.status == true) "Baku" else "AI"
            )
            insertWord(newWord)
            Result.success(newWord)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getTokens(): Flow<Long> {
        return queries.getTokens().asFlow().mapToOne(Dispatchers.IO)
    }

    override suspend fun addTokens(amount: Long) {
        queries.addTokens(amount)
    }

    override suspend fun useToken(): Boolean {
        val current = queries.getTokens().executeAsOne()
        return if (current > 0) {
            queries.useToken()
            true
        } else false
    }

    override suspend fun getRandomWords(limit: Long): List<Word> {
        return queries.getRandomWords(limit).executeAsList().map { it.toDomain() }
    }
}
