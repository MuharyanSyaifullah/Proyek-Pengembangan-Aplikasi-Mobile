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
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            when (uiState) {
                is DetailUiState.Loading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                is DetailUiState.Error -> Text((uiState as DetailUiState.Error).message, modifier = Modifier.align(Alignment.Center))
                is DetailUiState.Success -> {
                    val word = (uiState as DetailUiState.Success).word
                    Column(
                        modifier = Modifier.padding(16.dp).fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(word.term, style = MaterialTheme.typography.displayMedium)
                        SuggestionChip(onClick = {}, label = { Text(word.category) })
                        HorizontalDivider()
                        Text("Definisi:", style = MaterialTheme.typography.titleLarge)
                        Text(word.definition, style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }
        }
    }
}
