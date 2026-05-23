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
    onWordClick: (String) -> Unit,
    onNavigateToGacha: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToQuiz: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val isSearching by viewModel.isSearching.collectAsState()
    val searchError by viewModel.searchError.collectAsState()
    val tokens by viewModel.tokens.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text("Pusaka Kata", style = MaterialTheme.typography.headlineMedium) 
                },
                actions = {
                    Badge(
                        containerColor = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text("$tokens 🪙", modifier = Modifier.padding(4.dp))
                    }
                    IconButton(onClick = onNavigateToSettings) { 
                        Icon(Icons.Default.Settings, contentDescription = "Pengaturan") 
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            // Search Bar - Satu-satunya cara cari/tambah
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.onSearchQueryChange(it) },
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                placeholder = { Text("Tanyakan makna pusaka...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    if (isSearching) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp))
                    } else if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.executeSearch() }) {
                            Icon(Icons.Default.Send, contentDescription = "Kirim")
                        }
                    }
                },
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                isError = searchError != null
            )

            if (searchError != null) {
                Text(
                    text = searchError!!,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(horizontal = 16.dp),
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                when (val state = uiState) {
                    is HomeUiState.Loading -> LoadingIndicator()
                    is HomeUiState.Empty -> EmptyState(
                        message = "Pusaka masih kosong. Ketik kata di atas untuk mulai memanggil AI!",
                    )
                    is HomeUiState.Error -> ErrorMessage(message = state.message)
                    is HomeUiState.Success -> {
                        val words = state.words
                        if (words.isEmpty() && searchQuery.isNotEmpty()) {
                            EmptyState(message = "Tekan ikon kirim untuk memanggil makna '$searchQuery' dari AI.")
                        } else {
                            LazyColumn(
                                contentPadding = PaddingValues(16.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                item {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Button(
                                            onClick = onNavigateToQuiz,
                                            modifier = Modifier.weight(1f),
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = MaterialTheme.colorScheme.secondaryContainer, 
                                                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                                            ),
                                            shape = RoundedCornerShape(12.dp)
                                        ) {
                                            Icon(Icons.Default.Quiz, contentDescription = null)
                                            Spacer(Modifier.width(8.dp))
                                            Text("Mulai Kuis")
                                        }
                                        Button(
                                            onClick = onNavigateToGacha,
                                            modifier = Modifier.weight(1f),
                                            shape = RoundedCornerShape(12.dp)
                                        ) {
                                            Icon(Icons.Default.Casino, contentDescription = null)
                                            Spacer(Modifier.width(8.dp))
                                            Text("Gacha")
                                        }
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
