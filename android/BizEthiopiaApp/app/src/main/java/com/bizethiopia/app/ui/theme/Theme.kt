package com.bizethiopia.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val BizEthiopiaLightColors = lightColorScheme(
    primary = Color(0xFF1465E8),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE8F1FF),
    onPrimaryContainer = Color(0xFF0A1B30),
    secondary = Color(0xFFFFD21F),
    onSecondary = Color(0xFF132238),
    secondaryContainer = Color(0xFFFFF3C7),
    background = Color(0xFFF6F8FC),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF132238),
    onSurfaceVariant = Color(0xFF6D7B91),
    outline = Color(0xFFE7EBF2),
    error = Color(0xFFE34B4B),
)

@Composable
fun BizEthiopiaTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = BizEthiopiaLightColors,
        content = content
    )
}
