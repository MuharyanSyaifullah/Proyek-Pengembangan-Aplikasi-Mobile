package id.pusakakata.ui.screens.quiz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.pusakakata.domain.model.Word
import id.pusakakata.domain.repository.ItemRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed interface QuizUiState {
    object Loading : QuizUiState
    data class Question(
        val word: Word,
        val options: List<String>,
        val correctAnswer: String
    ) : QuizUiState
    object Finished : QuizUiState
    object Empty : QuizUiState
}

class QuizViewModel(private val repository: ItemRepository) : ViewModel() {
    private val _uiState = MutableStateFlow<QuizUiState>(QuizUiState.Loading)
    val uiState: StateFlow<QuizUiState> = _uiState

    init {
        generateQuestion()
    }

    fun generateQuestion() {
        viewModelScope.launch {
            _uiState.value = QuizUiState.Loading
            val allWords = repository.getRandomWords(5)
            if (allWords.size < 3) {
                _uiState.value = QuizUiState.Empty
                return@launch
            }

            val correctWord = allWords.random()
            val options = (allWords.filter { it != correctWord }.take(2).map { it.definition } + correctWord.definition).shuffled()

            _uiState.value = QuizUiState.Question(
                word = correctWord,
                options = options,
                correctAnswer = correctWord.definition
            )
        }
    }

    fun submitAnswer(answer: String) {
        val state = _uiState.value
        if (state is QuizUiState.Question) {
            viewModelScope.launch {
                if (answer == state.correctAnswer) {
                    repository.addTokens(10) // Reward token
                }
                // SRS feedback popup would be here
                _uiState.value = QuizUiState.Finished
            }
        }
    }
}
