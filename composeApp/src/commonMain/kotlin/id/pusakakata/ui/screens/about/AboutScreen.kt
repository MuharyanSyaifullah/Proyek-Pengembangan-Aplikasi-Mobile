package id.pusakakata.ui.screens.about

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tentang PusakaKata") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Text("<-")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "PusakaKata",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "v1.0.0",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Aplikasi pembelajaran kosa kata bahasa Indonesia dengan sistem Spaced Repetition (SRS) dan elemen gamifikasi kartu pusaka nusantara.",
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "Dibuat dengan ❤️ oleh Tim PusakaKata",
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}
