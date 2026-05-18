package id.pusakakata.ui.screens.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import id.pusakakata.ui.components.EmptyState
import id.pusakakata.ui.components.LoadingIndicator
import id.pusakakata.ui.components.ErrorMessage
import id.pusakakata.ui.components.ItemCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onAddWord: () -> Unit,
    onWordClick: (String) -> Unit,
    onNavigateToGacha: () -> Unit,
    onNavigateToAbout: () -> Unit,
    onNavigateToFlashcard: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text("Pusaka Kata", style = MaterialTheme.typography.headlineMedium) 
                },
                actions = {
                    IconButton(onClick = onNavigateToFlashcard) {
                        Icon(Icons.Default.School, contentDescription = "Belajar")
                    }
                    IconButton(onClick = onNavigateToGacha) { 
                        Icon(Icons.Default.Casino, contentDescription = "Gacha") 
                    }
                    IconButton(onClick = onNavigateToAbout) { 
                        Icon(Icons.Default.Info, contentDescription = "Tentang") 
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onAddWord,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                icon = { Icon(Icons.Default.Add, "Tambah") },
                text = { Text("Tambah Kata") }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            when (val state = uiState) {
                is HomeUiState.Loading -> LoadingIndicator()
                is HomeUiState.Empty -> EmptyState(
                    message = "Belum ada kosakata.",
                    onAction = onAddWord,
                    actionLabel = "Mulai Tambah"
                )
                is HomeUiState.Error -> ErrorMessage(message = state.message)
                is HomeUiState.Success -> {
                    val words = state.words
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        item {
                            Button(
                                onClick = onNavigateToFlashcard,
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.School, contentDescription = null)
                                Spacer(Modifier.width(8.dp))
                                Text("Asah Pusaka Hari Ini")
                            }
                        }
                        items(words, key = { it.id }) { word ->
                            ItemCard(
                                word = word,
                                onClick = { onWordClick(word.id) },
                                onDelete = { viewModel.deleteWord(word.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}
