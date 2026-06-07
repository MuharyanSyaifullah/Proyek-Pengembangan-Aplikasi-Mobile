package id.pusakakata.ui.screens.flashcard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
fun FlashcardScreen(
    viewModel: FlashcardViewModel,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Asah Pusaka") },
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
                        listOf(MaterialTheme.colorScheme.background, MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.2f))
                    )
                )
        ) {
            when (val state = uiState) {
                is FlashcardUiState.Loading -> LoadingIndicator()
                is FlashcardUiState.Empty -> EmptyState(
                    message = "Semua pusaka sudah diasah! Datang lagi besok.",
                    onAction = onBack,
                    actionLabel = "Kembali"
                )
                is FlashcardUiState.Success -> {
                    if (state.isFinished) {
                        EmptyState(
                            message = "Sesi belajar hari ini selesai! Ilmu Anda bertambah sakti.",
                            onAction = onBack,
                            actionLabel = "Kembali ke Menu"
                        )
                    } else {
                        Column(
                            modifier = Modifier.fillMaxSize().padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                "MENGHAFAL ${state.currentIndex + 1} DARI ${state.words.size}",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                ),
                                color = MaterialTheme.colorScheme.outline
                            )
                            
                            Spacer(Modifier.height(32.dp))
                            
                            val currentWord = state.currentWord
                            if (currentWord != null) {
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(380.dp),
                                    onClick = { viewModel.flipCard() },
                                    shape = RoundedCornerShape(32.dp),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (state.isFlipped) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.primary
                                    )
                                ) {
                                    Box(
                                        modifier = Modifier.fillMaxSize().padding(32.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (state.isFlipped) {
                                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                Text(
                                                    text = "MAKNA",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = MaterialTheme.colorScheme.secondary
                                                )
                                                Spacer(Modifier.height(16.dp))
                                                Text(
                                                    text = currentWord.definition,
                                                    style = MaterialTheme.typography.bodyLarge.copy(lineHeight = 28.sp),
                                                    textAlign = TextAlign.Center
                                                )
                                            }
                                        } else {
                                            Text(
                                                text = currentWord.term,
                                                style = MaterialTheme.typography.displayMedium,
                                                color = MaterialTheme.colorScheme.onPrimary,
                                                fontWeight = FontWeight.Black,
                                                textAlign = TextAlign.Center
                                            )
                                        }
                                    }
                                }
                                
                                Spacer(Modifier.height(48.dp))

                                if (state.isFlipped) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        FlashcardActionButton("Sulit", MaterialTheme.colorScheme.tertiary, Modifier.weight(1f)) { viewModel.nextCard(1) }
                                        FlashcardActionButton("Sedang", MaterialTheme.colorScheme.secondary, Modifier.weight(1f)) { viewModel.nextCard(3) }
                                        FlashcardActionButton("Mudah", MaterialTheme.colorScheme.primary, Modifier.weight(1f)) { viewModel.nextCard(5) }
                                    }
                                } else {
                                    Text(
                                        "Ketuk kartu untuk melihat arti",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.outline,
                                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FlashcardActionButton(label: String, color: androidx.compose.ui.graphics.Color, modifier: Modifier, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(containerColor = color)
    ) {
        Text(label, fontWeight = FontWeight.Bold)
    }
}
