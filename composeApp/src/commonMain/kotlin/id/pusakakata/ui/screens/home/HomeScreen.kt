package id.pusakakata.ui.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
    onNavigateToFlashcard: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text("Pusaka Kata") 
                },
                actions = {
                    IconButton(onClick = onNavigateToFlashcard) {
                        Icon(Icons.Default.School, contentDescription = "Belajar")
                    }
                    IconButton(onClick = onNavigateToGacha) { 
                        Icon(Icons.Default.Casino, contentDescription = "Gacha") 
                    }
                    IconButton(onClick = onNavigateToSettings) { 
                        Icon(Icons.Default.Settings, contentDescription = "Pengaturan") 
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
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            // Search Bar (Requirement 25%)
            if (uiState !is HomeUiState.Loading && uiState !is HomeUiState.Error) {
                OutlinedTextField(
                    value = if (uiState is HomeUiState.Success) (uiState as HomeUiState.Success).searchQuery else "",
                    onValueChange = { viewModel.onSearchQueryChange(it) },
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    placeholder = { Text("Cari pusaka atau makna...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    trailingIcon = {
                        if (uiState is HomeUiState.Success && (uiState as HomeUiState.Success).searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.onSearchQueryChange("") }) {
                                Icon(Icons.Default.Clear, contentDescription = "Hapus")
                            }
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )
            }

            Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
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
                        if (words.isEmpty() && state.searchQuery.isNotEmpty()) {
                            EmptyState(message = "Tidak ada hasil untuk '${state.searchQuery}'")
                        } else {
                            LazyColumn(
                                contentPadding = PaddingValues(bottom = 80.dp, start = 16.dp, end = 16.dp),
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
    }
}
