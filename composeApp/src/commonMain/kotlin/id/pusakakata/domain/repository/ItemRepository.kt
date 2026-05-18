package id.pusakakata.domain.repository

import id.pusakakata.domain.model.Word
import kotlinx.coroutines.flow.Flow

interface ItemRepository {
    fun getAllWords(): Flow<List<Word>>
    suspend fun getWordById(id: String): Word?
    suspend fun insertWord(word: Word)
    suspend fun updateWord(word: Word)
    suspend fun deleteWord(id: String)
    suspend fun searchOnline(word: String): Result<Word>
}
