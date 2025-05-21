
package com.example.mony.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Suas paletas de cores (já definidas)
private val DarkColorScheme = darkColorScheme(
    primary = RoxoDark,
    onPrimary = GrayTop,
    secondary = GrayDark,
    onSecondary = White,
    background = BlackMenos,//fundo
    surface = Black,
    onBackground = Gray,
    onSurface = White,

    //Gradiente
    surfaceContainerLow = Roxo,
    surfaceContainerLowest = RoxoMedio,
    surfaceContainerHighest = RoxoDark,

)

private val LightColorScheme = lightColorScheme(
    primary = Amarelo, //cores padrão
    onPrimary = White, //topappbar
    secondary = GrayMenos, //cinzas humildes
    onSecondary = Black, //textos...coisas assim
    background = White,//fundo
    surface = White,
    onBackground = Gray,
    onSurface = TextColor,
    onError = Gray,

    //Gradiente
    surfaceContainerLow = AmareloClaro,
    surfaceContainerLowest = Amarelo,
    surfaceContainerHighest = AmareloMedio,
)

// Este é o composable do seu tema
@Composable
fun MonyTheme( // O nome do seu tema é MonyTheme
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography, // Supondo que você tenha uma tipografia definida
        content = content
    )
}


