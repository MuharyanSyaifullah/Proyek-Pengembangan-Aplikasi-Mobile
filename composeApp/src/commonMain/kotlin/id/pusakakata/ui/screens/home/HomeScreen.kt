package id.pusakakata.ui.screens.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Casino
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
                icon = { Icon(Icons.Default.Add, "Tambah") },
                text = { Text("Tambah Kata") }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            when (uiState) {
                is HomeUiState.Loading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                is HomeUiState.Empty -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Belum ada kosakata.", style = MaterialTheme.typography.bodyLarge)
                        Button(onClick = onAddWord, modifier = Modifier.padding(top = 8.dp)) {
                            Text("Mulai Tambah")
                        }
                    }
                }
                is HomeUiState.Error -> Text((uiState as HomeUiState.Error).message, modifier = Modifier.align(Alignment.Center))
                is HomeUiState.Success -> {
                    val words = (uiState as HomeUiState.Success).words
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(words) { word ->
                            OutlinedCard(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onWordClick(word.id) }
                            ) {
                                ListItem(
                                    headlineContent = { 
                                        Text(word.term, style = MaterialTheme.typography.titleLarge) 
                                    },
                                    supportingContent = { 
                                        Text(
                                            word.definition, 
                                            maxLines = 2,
                                            style = MaterialTheme.typography.bodyMedium 
                                        ) 
                                    },
                                    overlineContent = {
                                        Text(word.category, style = MaterialTheme.typography.labelSmall)
                                    },
                                    trailingContent = {
                                        IconButton(onClick = { viewModel.deleteWord(word.id) }) {
                                            Icon(
                                                Icons.Default.Delete, 
                                                contentDescription = "Hapus",
                                                tint = MaterialTheme.colorScheme.error
                                            )
                                        }
                                    },
                                    colors = ListItemDefaults.colors(containerColor = androidx.compose.ui.graphics.Color.Transparent)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
