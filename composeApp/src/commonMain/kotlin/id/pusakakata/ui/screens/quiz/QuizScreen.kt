package id.pusakakata.ui.screens.quiz

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import id.pusakakata.ui.components.EmptyState
import id.pusakakata.ui.components.LoadingIndicator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizScreen(
    viewModel: QuizViewModel,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var showSrsPopup by remember { mutableStateOf(false) }
    var lastAnswerResult by remember { mutableStateOf<Boolean?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Kuis Pusaka") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            when (val state = uiState) {
                is QuizUiState.Loading -> LoadingIndicator()
                is QuizUiState.Empty -> EmptyState(
                    message = "Cari minimal 3 kata dulu di beranda untuk memulai kuis!",
                    onAction = onBack,
                    actionLabel = "Kembali"
                )
                is QuizUiState.Question -> {
                    Column(
                        modifier = Modifier.fillMaxSize().padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Apa arti dari kata:", style = MaterialTheme.typography.titleMedium)
                        Text(
                            state.word.term,
                            style = MaterialTheme.typography.displayMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(Modifier.height(32.dp))

                        state.options.forEach { option ->
                            Button(
                                onClick = { 
                                    lastAnswerResult = (option == state.correctAnswer)
                                    viewModel.submitAnswer(option)
                                    showSrsPopup = true
                                },
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                shape = MaterialTheme.shapes.medium,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant, 
                                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            ) {
                                Text(option, modifier = Modifier.padding(8.dp))
                            }
                        }
                    }
                }
                is QuizUiState.Finished -> {
                    if (showSrsPopup) {
                        AlertDialog(
                            onDismissRequest = { /* Force selection */ },
                            title = { 
                                Text(if (lastAnswerResult == true) "Jawaban Benar! 🎉" else "Jawaban Salah ❌")
                            },
                            text = { 
                                Column {
                                    Text(if (lastAnswerResult == true) "Selamat! Anda mendapat +10 Token." else "Tetap semangat belajar!")
                                    Spacer(Modifier.height(16.dp))
                                    Text("Seberapa sulit kata ini bagi Anda?", style = MaterialTheme.typography.labelLarge)
                                }
                            },
                            confirmButton = {
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Button(
                                        onClick = { viewModel.generateQuestion(); showSrsPopup = false },
                                        modifier = Modifier.fillMaxWidth()
                                    ) { Text("Mudah") }
                                    Button(
                                        onClick = { viewModel.generateQuestion(); showSrsPopup = false },
                                        modifier = Modifier.fillMaxWidth()
                                    ) { Text("Sedang") }
                                    Button(
                                        onClick = { viewModel.generateQuestion(); showSrsPopup = false },
                                        modifier = Modifier.fillMaxWidth()
                                    ) { Text("Susah") }
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}
