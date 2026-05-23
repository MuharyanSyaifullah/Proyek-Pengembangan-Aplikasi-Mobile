package id.pusakakata.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.pusakakata.domain.repository.ItemRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: ItemRepository) : ViewModel() {
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching.asStateFlow()

    private val _searchError = MutableStateFlow<String?>(null)
    val searchError: StateFlow<String?> = _searchError.asStateFlow()

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
                if (words.isEmpty() && !searching && query.isEmpty()) {
                    HomeUiState.Empty
                } else {
                    val filtered = if (searching || query.isEmpty()) {
                        words
                    } else {
                        words.filter { 
                            it.term.contains(query, ignoreCase = true) || 
                            it.definition.contains(query, ignoreCase = true) 
                        }
                    }
                    HomeUiState.Success(filtered)
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
        _searchError.value = null // Reset error saat mulai ngetik lagi
    }

    fun executeSearch() {
        val query = _searchQuery.value
        if (query.isBlank()) return

        viewModelScope.launch {
            _isSearching.value = true
            _searchError.value = null
            repository.searchAndSave(query)
                .onFailure { 
                    _searchError.value = "Pusaka '$query' tidak ditemukan di jagat maya." 
                }
                .onSuccess {
                    _searchQuery.value = "" // Reset setelah ketemu dan tersimpan
                }
            _isSearching.value = false
        }
    }

    fun deleteWord(id: String) {
        viewModelScope.launch {
            repository.deleteWord(id)
        }
    }
}
