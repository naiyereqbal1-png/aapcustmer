package com.tryathome.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = Indigo600,
    onPrimary = Color.White,
    secondary = Amber500,
    onSecondary = Slate900,
    background = Slate50,
    onBackground = Slate900,
    surface = Color.White,
    onSurface = Slate900,
    error = Rose600,
    onError = Color.White
)

@Composable
fun TRYatHOMETheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // We enforce light mode theme to match our elegant light styling of the web storefront!
    MaterialTheme(
        colorScheme = LightColorScheme,
        content = content
    )
}
