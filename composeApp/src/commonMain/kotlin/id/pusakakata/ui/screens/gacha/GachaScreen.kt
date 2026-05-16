package id.pusakakata.ui.screens.gacha

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import id.pusakakata.domain.model.LegendaryCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GachaScreen(
    viewModel: GachaViewModel,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pusaka Gacha") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        // Use a generic back icon or text if icons aren't available
                        Text("<-")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            when (val state = uiState) {
                is GachaUiState.Idle -> {
                    Text("Tarik Pusaka Keberuntunganmu!")
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { viewModel.drawCard() }) {
                        Text("Tarik Gacha")
                    }
                }
                is GachaUiState.Drawing -> {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Memanggil Pusaka...")
                }
                is GachaUiState.Result -> {
                    CardResult(card = state.card)
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { viewModel.reset() }) {
                        Text("Tarik Lagi")
                    }
                }
            }
        }
    }
}

@Composable
fun CardResult(card: LegendaryCard) {
    Card(
        modifier = Modifier.padding(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = card.rarity.displayName, style = MaterialTheme.typography.labelMedium)
            Text(text = card.name, style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = card.description)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Asal: ${card.origin}", style = MaterialTheme.typography.bodySmall)
        }
    }
}
