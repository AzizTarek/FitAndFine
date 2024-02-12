package com.example.fitandfine_project.ui.theme
import android.graphics.Color
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable

private val DarkColorPalette = darkColors(
//    primary = Purple200,
//    primaryVariant = Purple700,
//    secondary = Teal200
//    ,surface = Purple50,
//    background = Purple100
    background = androidx.compose.ui.graphics.Color.Black,
    surface = Pink700,
    onSurface = White,
    primary = Pink900,
    onPrimary = White,
    secondary = Pink100
)

private val LightColorPalette = lightColors(
//    primary = Purple500,
//    primaryVariant = Purple700,
//    secondary = Teal200,
//    surface = Purple50,
//    background = Purple100

    primary = Pink500,
    primaryVariant = Pink700,
    secondary = Green400,
    surface = Pink200,
    background = Pink50,
    onSurface = androidx.compose.ui.graphics.Color.Black,


//    primary = Green500,
//    primaryVariant = Green700,
//    secondary = Pink400,
//    surface = Green50,
//    background = Green100
)

@Composable
fun FitAndFine_ProjectTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}