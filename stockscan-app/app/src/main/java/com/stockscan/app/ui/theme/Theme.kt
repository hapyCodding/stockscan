package com.stockscan.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val colorScheme =
    lightColorScheme(
        primary = Color(0xFF1B5E20),
        secondary = Color(0xFF00695C),
    )

@Composable
fun StockScanTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = colorScheme, content = content)
}
