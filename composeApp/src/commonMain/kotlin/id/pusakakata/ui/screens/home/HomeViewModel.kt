package id.pusakakata.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.pusakakata.domain.repository.ItemRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: ItemRepository) : ViewModel() {
    private val _searchQuery = MutableStateFlow("")
    private val _isSearching = MutableStateFlow(false)
    private val _errorMessage = MutableStateFlow<String?>(null)

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState

    val tokens: StateFlow<Long> = repository.getTokens()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0L)

    init {
        observeWords()
    }

    private fun observeWords() {
        viewModelScope.launch {
            combine(repository.getAllWords(), _searchQuery, _isSearching) { words, query, searching ->
                if (words.isEmpty() && !searching) {
                    HomeUiState.Empty
                } else {
                    val filtered = if (query.isBlank() || searching) {
                        words
                    } else {
                        words.filter { 
                            it.term.contains(query, ignoreCase = true) || 
                            it.definition.contains(query, ignoreCase = true) 
                        }
                    }
                    HomeUiState.Success(filtered, query, searching)
                }
            }.catch { 
                _uiState.value = HomeUiState.Error(it.message ?: "Unknown Error") 
            }.collect { 
                _uiState.value = it 
            }
        }
    }

    fun onSearchQueryChange(newQuery: String) {
        _searchQuery.value = newQuery
    }

    fun executeSearch() {
        val query = _searchQuery.value
        if (query.isBlank()) return

        viewModelScope.launch {
            _isSearching.value = true
            repository.searchAndSave(query)
                .onFailure { _errorMessage.value = it.message }
            _isSearching.value = false
            _searchQuery.value = "" // Reset search bar after successful find
        }
    }

    fun deleteWord(id: String) {
        viewModelScope.launch {
            repository.deleteWord(id)
        }
    }
}
