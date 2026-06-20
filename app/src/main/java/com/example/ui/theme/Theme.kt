package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme =
  darkColorScheme(
    primary = SigeonBlue,
    secondary = SigeonCyan,
    tertiary = SigeonGreen,
    background = androidx.compose.ui.graphics.Color(0xFF0A192F),
    surface = androidx.compose.ui.graphics.Color(0xFF172A45),
    onPrimary = androidx.compose.ui.graphics.Color.White,
    onBackground = androidx.compose.ui.graphics.Color.White,
    onSurface = androidx.compose.ui.graphics.Color.White
  )

private val LightColorScheme =
  lightColorScheme(
    primary = SigeonBlue,
    secondary = SigeonCyan,
    tertiary = SigeonGreen,
    background = SigeonLightBg,
    surface = SigeonSurface,
    onPrimary = androidx.compose.ui.graphics.Color.White,
    onBackground = SigeonTextDark,
    onSurface = SigeonTextDark
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  // For Frutiger Aero brand consistency, default dynamicColor to false
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme =
    when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }

      darkTheme -> DarkColorScheme
      else -> LightColorScheme
    }

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
