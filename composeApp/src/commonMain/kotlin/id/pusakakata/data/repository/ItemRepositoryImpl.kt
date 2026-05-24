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
import kotlinx.serialization.json.Json
import kotlinx.serialization.decodeFromString
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.plus
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
            example = word.example,
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
            example = word.example,
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

    override suspend fun updateSrs(wordId: String, quality: Int) {
        val word = getWordById(wordId) ?: return
        val srs = word.srsData
        
        // SM-2 Algorithm logic
        val newEaseFactor = (srs.easeFactor + (0.1 - (5 - quality) * (0.08 + (5 - quality) * 0.02)))
            .coerceAtLeast(1.3)
        
        val newInterval = when {
            quality < 3 -> 1
            srs.intervalDays == 0 -> 1
            srs.intervalDays == 1 -> 6
            else -> (srs.intervalDays * newEaseFactor).toInt()
        }
        
        val nextReview = Clock.System.now().plus(newInterval, DateTimeUnit.DAY, kotlinx.datetime.TimeZone.currentSystemDefault())
        
        val updatedWord = word.copy(
            srsData = srs.copy(
                intervalDays = newInterval,
                easeFactor = newEaseFactor,
                nextReview = nextReview,
                level = if (quality >= 3) srs.level + 1 else 0
            )
        )
        updateWord(updatedWord)
    }

    @OptIn(ExperimentalUuidApi::class)
    override suspend fun searchAndSave(word: String): Result<Word> {
        return try {
            val rawResponse = getAiDefinition(word).getOrThrow()
            
            var definition = rawResponse
            var category = "Umum"
            var example = ""

            try {
                val parsed = Json { ignoreUnknownKeys = true }.decodeFromString<id.pusakakata.ui.screens.addedit.AiResponse>(rawResponse)
                definition = parsed.definition
                val cat = parsed.category
                category = if (cat == "Umum" || cat == "Sastra" || cat == "Arkais") cat else "Umum"
                example = parsed.example
            } catch (e: Exception) {
                // Gunakan default jika parsing gagal
            }

            val newWord = Word(
                id = Uuid.random().toString(),
                term = word.replaceFirstChar { it.uppercase() },
                definition = definition,
                category = category,
                example = example
            )
            insertWord(newWord)
            Result.success(newWord)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getAiDefinition(word: String): Result<String> {
        return try {
            val response = geminiService.generateDefinition(word)
            Result.success(response)
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
