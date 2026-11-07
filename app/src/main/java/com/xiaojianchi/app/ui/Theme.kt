package com.xiaojianchi.app.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp

val Paper = Color(0xFFF8F3EA)
val PaperLight = Color(0xFFFFFDF8)
val PaperDeep = Color(0xFFF0EADF)
val Ink = Color(0xFF3E3932)
val Muted = Color(0xFF8B8276)
val Sage = Color(0xFF6F826C)
val SageSoft = Color(0xFFEEF0E6)
val LineColor = Color(0xFFDED7CA)
val HandwrittenGold = Color(0xFFC6AB84)
val OnSage = Color(0xFFFFFDF8)

val HabitColors =
    listOf(
        Color(0xFFB87452),
        Color(0xFF5C8371),
        Color(0xFF7B6F9D),
        Color(0xFFB2934F),
        Color(0xFF4E7E91),
    )

private val JournalColors =
    lightColorScheme(
        primary = Sage,
        onPrimary = OnSage,
        primaryContainer = SageSoft,
        onPrimaryContainer = Ink,
        secondary = HandwrittenGold,
        onSecondary = Ink,
        background = Paper,
        onBackground = Ink,
        surface = PaperLight,
        onSurface = Ink,
        surfaceVariant = PaperDeep,
        onSurfaceVariant = Muted,
        outline = LineColor,
        outlineVariant = LineColor,
    )

private val JournalTypography =
    Typography(
        displaySmall = Typography().displaySmall.copy(fontFamily = FontFamily.Serif),
        headlineLarge = Typography().headlineLarge.copy(fontFamily = FontFamily.Serif),
        headlineMedium = Typography().headlineMedium.copy(fontFamily = FontFamily.Serif),
        headlineSmall = Typography().headlineSmall.copy(fontFamily = FontFamily.Serif),
        titleLarge = Typography().titleLarge.copy(fontFamily = FontFamily.Serif),
    )

@Composable
fun LittleStepsTheme(content: @Composable () -> Unit) {
    isSystemInDarkTheme() // Always retain the warm paper direction in v1.
    MaterialTheme(
        colorScheme = JournalColors,
        typography = JournalTypography,
        content = content,
    )
}

@Composable
fun PaperTexture(modifier: Modifier = Modifier) {
    Canvas(modifier.fillMaxSize()) {
        val step = 28.dp.toPx()
        var x = 0f
        while (x < size.width) {
            var y = 0f
            while (y < size.height) {
                drawCircle(Ink.copy(alpha = 0.025f), radius = 0.55.dp.toPx(), center = Offset(x, y))
                y += step
            }
            x += step
        }
    }
}
