package id.pusakakata.ui.screens.addedit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.pusakakata.domain.model.Word
import id.pusakakata.domain.repository.ItemRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class AddEditViewModel(
    private val repository: ItemRepository,
    private val wordId: String?
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddEditUiState())
    val uiState: StateFlow<AddEditUiState> = _uiState.asStateFlow()

    init {
        if (wordId != null) {
            loadWord(wordId)
        }
    }

    private fun loadWord(id: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, wordId = id) }
            repository.getWordById(id)?.let { word ->
                _uiState.update { 
                    it.copy(
                        term = word.term,
                        definition = word.definition,
                        category = word.category,
                        isLoading = false
                    )
                }
            } ?: _uiState.update { it.copy(isLoading = false, error = "Kata tidak ditemukan") }
        }
    }

    fun onTermChange(newTerm: String) {
        _uiState.update { it.copy(term = newTerm) }
    }

    fun onDefinitionChange(newDef: String) {
        _uiState.update { it.copy(definition = newDef) }
    }

    fun onCategoryChange(newCat: String) {
        _uiState.update { it.copy(category = newCat) }
    }

    fun searchOnline() {
        val term = _uiState.value.term
        if (term.isBlank()) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            repository.searchAndSave(term)
                .onSuccess { word ->
                    _uiState.update { 
                        it.copy(
                            definition = word.definition,
                            isLoading = false
                        )
                    }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isLoading = false, error = e.message) }
                }
        }
    }

    @OptIn(ExperimentalUuidApi::class)
    fun saveWord() {
        if (!_uiState.value.canSave) return
        
        viewModelScope.launch {
            val currentState = _uiState.value
            val word = Word(
                id = wordId ?: Uuid.random().toString(),
                term = currentState.term.trim(),
                definition = currentState.definition.trim(),
                category = currentState.category.trim()
            )
            try {
                if (wordId == null) {
                    repository.insertWord(word)
                } else {
                    repository.updateWord(word)
                }
                _uiState.update { it.copy(isSuccess = true) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }
}
