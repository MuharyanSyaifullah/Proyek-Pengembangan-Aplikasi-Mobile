package id.pusakakata.ui.screens.addedit

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditScreen(
    viewModel: AddEditViewModel,
    onBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(state.isSuccess) {
        if (state.isSuccess) {
            onBack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (state.term.isEmpty()) "Tambah Kata" else "Edit Kata") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        },
        floatingActionButton = {
            if (state.canSave) {
                FloatingActionButton(onClick = { viewModel.saveWord() }) {
                    Icon(Icons.Default.Check, contentDescription = "Simpan")
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (state.error != null) {
                Text(state.error!!, color = MaterialTheme.colorScheme.error)
            }
            
            OutlinedTextField(
                value = state.term,
                onValueChange = { viewModel.onTermChange(it) },
                label = { Text("Kosakata") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = state.term.isBlank() && state.term.isNotEmpty()
            )
            OutlinedTextField(
                value = state.definition,
                onValueChange = { viewModel.onDefinitionChange(it) },
                label = { Text("Definisi") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                isError = state.definition.isBlank() && state.definition.isNotEmpty()
            )
            
            Text("Pilih Kategori", style = MaterialTheme.typography.labelLarge)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("Umum", "Sastra", "Arkais").forEach { cat ->
                    FilterChip(
                        selected = state.category == cat,
                        onClick = { viewModel.onCategoryChange(cat) },
                        label = { Text(cat) }
                    )
                }
            }
        }
    }
}
