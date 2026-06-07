package id.pusakakata.ui.screens.quiz

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import id.pusakakata.ui.components.EmptyState
import id.pusakakata.ui.components.LoadingIndicator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizScreen(
    viewModel: QuizViewModel,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tantangan Pusaka") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = androidx.compose.ui.graphics.Color.Transparent)
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(MaterialTheme.colorScheme.background, MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f))
                    )
                )
        ) {
            when (val state = uiState) {
                is QuizUiState.Loading -> LoadingIndicator()
                is QuizUiState.Empty -> EmptyState(
                    message = "Pusaka anda belum cukup kuat. Cari minimal 3 kata di beranda untuk memulai kuis!",
                    onAction = onBack,
                    actionLabel = "Kembali ke Beranda"
                )
                is QuizUiState.Question -> {
                    Column(
                        modifier = Modifier.fillMaxSize().padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Surface(
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                state.quizMessage.uppercase(), 
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                        
                        Spacer(Modifier.height(32.dp))
                        Text("Apa makna sejati dari kata:", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.outline)
                        Spacer(Modifier.height(8.dp))
                        Text(
                            state.word.term,
                            style = MaterialTheme.typography.displayMedium,
                            color = MaterialTheme.colorScheme.primary,
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Black
                        )
                        
                        Spacer(Modifier.height(48.dp))

                        state.options.forEach { option ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                shape = RoundedCornerShape(20.dp),
                                onClick = { viewModel.submitAnswer(option) },
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surface
                                ),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Text(
                                    text = option, 
                                    modifier = Modifier.padding(20.dp), 
                                    textAlign = TextAlign.Center,
                                    style = MaterialTheme.typography.bodyLarge.copy(lineHeight = 24.sp),
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
                is QuizUiState.Finished -> {
                    AlertDialog(
                        onDismissRequest = { /* Force selection */ },
                        shape = RoundedCornerShape(28.dp),
                        title = { 
                            Text(
                                if (state.isCorrect) "Jawaban Benar! 🎉" else "Jawaban Kurang Tepat ❌",
                                fontWeight = FontWeight.Bold
                            )
                        },
                        text = { 
                            Column {
                                Text(
                                    if (state.isCorrect) "Selamat! Anda mendapat +10 Token untuk Gacha." 
                                    else "Jangan menyerah, terus asah ingatan anda!"
                                )
                                Spacer(Modifier.height(24.dp))
                                Text("Seberapa sulit tantangan ini bagi Anda?", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                            }
                        },
                        confirmButton = {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                DifficultyButton("Sangat Mudah", 5) { viewModel.updateSrsAndNext(5) }
                                DifficultyButton("Bisa Saya Ingat", 3) { viewModel.updateSrsAndNext(3) }
                                DifficultyButton("Sangat Sulit", 1) { viewModel.updateSrsAndNext(1) }
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun DifficultyButton(label: String, quality: Int, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = when(quality) {
                5 -> MaterialTheme.colorScheme.primary
                3 -> MaterialTheme.colorScheme.secondary
                else -> MaterialTheme.colorScheme.tertiary
            }
        )
    ) {
        Text(label, fontWeight = FontWeight.Bold)
    }
}
