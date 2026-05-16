package id.pusakakata.ui.screens.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onAddWord: () -> Unit,
    onWordClick: (String) -> Unit,
    onNavigateToGacha: () -> Unit,
    onNavigateToAbout: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pusaka Kata") },
                actions = {
                    TextButton(onClick = onNavigateToGacha) { Text("Gacha") }
                    TextButton(onClick = onNavigateToAbout) { Text("Info") }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddWord) {
                Icon(Icons.Default.Add, contentDescription = "Tambah Kata")
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            when (uiState) {
                is HomeUiState.Loading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                is HomeUiState.Empty -> Text("Belum ada kosakata.", modifier = Modifier.align(Alignment.Center))
                is HomeUiState.Error -> Text((uiState as HomeUiState.Error).message, modifier = Modifier.align(Alignment.Center))
                is HomeUiState.Success -> {
                    val words = (uiState as HomeUiState.Success).words
                    LazyColumn {
                        items(words) { word ->
                            ListItem(
                                headlineContent = { Text(word.term) },
                                supportingContent = { Text(word.definition) },
                                trailingContent = {
                                    IconButton(onClick = { viewModel.deleteWord(word.id) }) {
                                        Icon(Icons.Default.Delete, contentDescription = "Hapus")
                                    }
                                },
                                modifier = Modifier.clickable { onWordClick(word.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}
