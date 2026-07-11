package com.stockscan.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.stockscan.app.inventory.ItemDetailScreen
import com.stockscan.app.inventory.ItemListScreen
import com.stockscan.app.scan.ScanScreen
import com.stockscan.app.ui.theme.StockScanTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            StockScanTheme {
                StockScanNavHost()
            }
        }
    }
}

private object Routes {
    const val ITEMS = "items"
    const val SCAN = "scan"
    const val DETAIL = "detail/{barcode}"

    fun detail(barcode: String) = "detail/$barcode"
}

@Composable
private fun StockScanNavHost() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Routes.ITEMS) {
        composable(Routes.ITEMS) {
            ItemListScreen(
                onScan = { navController.navigate(Routes.SCAN) },
                onItemClick = { barcode -> navController.navigate(Routes.detail(barcode)) },
            )
        }
        composable(Routes.SCAN) {
            ScanScreen(
                onScanned = { barcode ->
                    navController.navigate(Routes.detail(barcode)) {
                        popUpTo(Routes.ITEMS)
                    }
                },
                onClose = { navController.popBackStack() },
            )
        }
        composable(
            route = Routes.DETAIL,
            arguments = listOf(navArgument("barcode") { type = NavType.StringType }),
        ) { entry ->
            val barcode = entry.arguments?.getString("barcode").orEmpty()
            ItemDetailScreen(
                barcode = barcode,
                onBack = { navController.popBackStack() },
            )
        }
    }
}
