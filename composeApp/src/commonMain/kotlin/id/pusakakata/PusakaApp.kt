package id.pusakakata

import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import id.pusakakata.presentation.navigation.AppNavHost
import id.pusakakata.presentation.theme.PusakaKataTheme

@Composable
fun PusakaApp() {
    PusakaKataTheme {
        val navController = rememberNavController()
        AppNavHost(navController = navController)
    }
}
