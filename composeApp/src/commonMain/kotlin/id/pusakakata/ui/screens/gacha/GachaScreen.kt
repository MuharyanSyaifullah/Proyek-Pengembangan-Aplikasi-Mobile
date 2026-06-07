package id.pusakakata.ui.screens.gacha

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import id.pusakakata.domain.model.LegendaryCard
import id.pusakakata.ui.components.LoadingIndicator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GachaScreen(
    viewModel: GachaViewModel,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val tokens by viewModel.tokens.collectAsState()
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pusaka Gacha") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                },
                actions = {
                    Text("$tokens 🪙", modifier = Modifier.padding(16.dp))
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                when (val state = uiState) {
                    is GachaUiState.Idle -> {
                        Text("Tarik Pusaka Keberuntunganmu!", style = MaterialTheme.typography.titleLarge)
                        Text("Biaya: 1 Token", style = MaterialTheme.typography.bodySmall)
                        Spacer(modifier = Modifier.height(24.dp))
                        Button(
                            onClick = { viewModel.drawCard() },
                            enabled = tokens > 0,
                            shape = MaterialTheme.shapes.large
                        ) {
                            Text("Tarik Gacha (1 🪙)")
                        }
                    }
                    is GachaUiState.Drawing -> {
                        LoadingIndicator()
                        Text("Memanggil Pusaka...")
                    }
                    is GachaUiState.Result -> {
                        CardResult(card = state.card)
                        Spacer(modifier = Modifier.height(24.dp))
                        Button(
                            onClick = { viewModel.reset() },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Tarik Lagi")
                        }
                    }
                    is GachaUiState.Error -> {
                        Text(state.message, color = MaterialTheme.colorScheme.error)
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = onBack) { Text("Cari Token di Kuis") }
                    }
                }
            }
        }
    }
}

@Composable
fun CardResult(card: LegendaryCard) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .background(
                    Brush.verticalGradient(
                        listOf(MaterialTheme.colorScheme.primary.copy(alpha = 0.05f), MaterialTheme.colorScheme.surface)
                    )
                )
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                color = when (card.rarity.name) {
                    "MYTHIC" -> MaterialTheme.colorScheme.tertiary
                    "EPIC" -> MaterialTheme.colorScheme.secondary
                    else -> MaterialTheme.colorScheme.primary
                },
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = card.rarity.displayName.uppercase(),
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color.White
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = card.name,
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = card.description,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                lineHeight = 24.sp
            )
            
            if (card.fullStory.isNotBlank()) {
                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(modifier = Modifier.padding(horizontal = 24.dp))
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = card.fullStory,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Justify,
                    style = MaterialTheme.typography.bodyMedium,
                    lineHeight = 20.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.LocationOn, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.secondary)
                Spacer(Modifier.width(4.dp))
                Text(text = "Asal: ${card.origin}", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.secondary)
            }
        }
    }
}
