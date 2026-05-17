package id.pusakakata.ui.screens.detail

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    viewModel: DetailViewModel,
    onBack: () -> Unit,
    onEdit: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detail Kata") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                },
                actions = {
                    if (uiState is DetailUiState.Success) {
                        IconButton(onClick = { onEdit((uiState as DetailUiState.Success).word.id) }) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit")
                        }
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when (val state = uiState) {
                is DetailUiState.Loading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                is DetailUiState.Error -> Text(state.message, modifier = Modifier.align(Alignment.Center))
                is DetailUiState.Success -> {
                    val word = state.word
                    Column(
                        modifier = Modifier
                            .padding(24.dp)
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Surface(
                            color = MaterialTheme.colorScheme.primaryContainer,
                            shape = MaterialTheme.shapes.medium
                        ) {
                            Text(
                                text = word.category,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                        
                        Text(
                            text = word.term,
                            style = MaterialTheme.typography.displayMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        
                        HorizontalDivider()
                        
                        Text(
                            text = "Definisi:",
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.secondary
                        )
                        
                        Text(
                            text = word.definition,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        // SRS Info Card
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("Informasi Belajar (SRS)", style = MaterialTheme.typography.titleMedium)
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Level: ${word.srsData.level}")
                                    Text("Ease: ${word.srsData.easeFactor}")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
