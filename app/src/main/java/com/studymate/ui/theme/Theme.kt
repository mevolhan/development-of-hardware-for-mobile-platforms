package com.studymate.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColors = lightColorScheme(
    primary = BlueAccent,
    secondary = WarmAccent,
    tertiary = MintAccent,
    background = Sand,
    surface = androidx.compose.ui.graphics.Color.White,
    onPrimary = androidx.compose.ui.graphics.Color.White,
    onBackground = Ink,
    onSurface = Ink
)

@Composable
fun StudyMateTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColors,
        typography = StudyMateTypography,
        content = content
    )
}
