package org.kenjinx.android.ui.theme

import android.app.Activity
import android.content.Context
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat

enum class ThemeMode(val displayName: String) {
    LIGHT("Light"),
    DARK("Dark"),
    SYSTEM("System")
}

private const val PREFS_NAME = "app_theme_prefs"
private const val KEY_THEME = "selected_theme"
private const val KEY_MODE = "theme_mode"

var themeRecomposeKey by mutableStateOf(0L)
    private set

fun triggerThemeRecompose() {
    themeRecomposeKey = System.currentTimeMillis()
}

fun saveThemePreference(context: Context, theme: AppThemeEnum) {
    context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        .edit()
        .putString(KEY_THEME, theme.name)
        .apply()
    triggerThemeRecompose()
}

fun saveThemeMode(context: Context, mode: ThemeMode) {
    context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        .edit()
        .putString(KEY_MODE, mode.name)
        .apply()
    triggerThemeRecompose()
}

fun getSavedTheme(context: Context): AppThemeEnum {
    val name = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        .getString(KEY_THEME, AppThemeEnum.GOLD.name) ?: AppThemeEnum.GOLD.name
    return try {
        AppThemeEnum.valueOf(name)
    } catch (_: Exception) {
        AppThemeEnum.GOLD
    }
}

fun getSavedThemeMode(context: Context): ThemeMode {
    val name = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        .getString(KEY_MODE, ThemeMode.SYSTEM.name) ?: ThemeMode.SYSTEM.name
    return try {
        ThemeMode.valueOf(name)
    } catch (_: Exception) {
        ThemeMode.SYSTEM
    }
}

private fun buildLightColorScheme(theme: Any): androidx.compose.material3.ColorScheme {
    return when (theme) {
        is Gold -> lightColorScheme(
            primary = Gold.LightPrimary, onPrimary = Gold.LightOnPrimary,
            primaryContainer = Gold.LightPrimaryContainer, onPrimaryContainer = Gold.LightOnPrimaryContainer,
            secondary = Gold.LightSecondary, onSecondary = Gold.LightOnSecondary,
            secondaryContainer = Gold.LightSecondaryContainer, onSecondaryContainer = Gold.LightOnSecondaryContainer,
            tertiary = Gold.LightTertiary, onTertiary = Gold.LightOnTertiary,
            tertiaryContainer = Gold.LightTertiaryContainer, onTertiaryContainer = Gold.LightOnTertiaryContainer,
            error = Gold.LightError, onError = Gold.LightOnError,
            errorContainer = Gold.LightErrorContainer, onErrorContainer = Gold.LightOnErrorContainer,
            background = Gold.LightSurface, onBackground = Gold.LightOnSurface,
            surface = Gold.LightSurface, onSurface = Gold.LightOnSurface,
            surfaceVariant = Gold.LightSurfaceVariant, onSurfaceVariant = Gold.LightOnSurfaceVariant,
            outline = Gold.LightOutline, outlineVariant = Gold.LightOutlineVariant,
            inverseSurface = Gold.LightInverseSurface, inverseOnSurface = Gold.LightInverseOnSurface,
            inversePrimary = Gold.LightInversePrimary, surfaceTint = Gold.LightSurfaceTint
        )
        is Blue -> lightColorScheme(
            primary = Blue.LightPrimary, onPrimary = Blue.LightOnPrimary,
            primaryContainer = Blue.LightPrimaryContainer, onPrimaryContainer = Blue.LightOnPrimaryContainer,
            secondary = Blue.LightSecondary, onSecondary = Blue.LightOnSecondary,
            secondaryContainer = Blue.LightSecondaryContainer, onSecondaryContainer = Blue.LightOnSecondaryContainer,
            tertiary = Blue.LightTertiary, onTertiary = Blue.LightOnTertiary,
            tertiaryContainer = Blue.LightTertiaryContainer, onTertiaryContainer = Blue.LightOnTertiaryContainer,
            error = Blue.LightError, onError = Blue.LightOnError,
            errorContainer = Blue.LightErrorContainer, onErrorContainer = Blue.LightOnErrorContainer,
            background = Blue.LightSurface, onBackground = Blue.LightOnSurface,
            surface = Blue.LightSurface, onSurface = Blue.LightOnSurface,
            surfaceVariant = Blue.LightSurfaceVariant, onSurfaceVariant = Blue.LightOnSurfaceVariant,
            outline = Blue.LightOutline, outlineVariant = Blue.LightOutlineVariant,
            inverseSurface = Blue.LightInverseSurface, inverseOnSurface = Blue.LightInverseOnSurface,
            inversePrimary = Blue.LightInversePrimary, surfaceTint = Blue.LightSurfaceTint
        )
        is Green -> lightColorScheme(
            primary = Green.LightPrimary, onPrimary = Green.LightOnPrimary,
            primaryContainer = Green.LightPrimaryContainer, onPrimaryContainer = Green.LightOnPrimaryContainer,
            secondary = Green.LightSecondary, onSecondary = Green.LightOnSecondary,
            secondaryContainer = Green.LightSecondaryContainer, onSecondaryContainer = Green.LightOnSecondaryContainer,
            tertiary = Green.LightTertiary, onTertiary = Green.LightOnTertiary,
            tertiaryContainer = Green.LightTertiaryContainer, onTertiaryContainer = Green.LightOnTertiaryContainer,
            error = Green.LightError, onError = Green.LightOnError,
            errorContainer = Green.LightErrorContainer, onErrorContainer = Green.LightOnErrorContainer,
            background = Green.LightSurface, onBackground = Green.LightOnSurface,
            surface = Green.LightSurface, onSurface = Green.LightOnSurface,
            surfaceVariant = Green.LightSurfaceVariant, onSurfaceVariant = Green.LightOnSurfaceVariant,
            outline = Green.LightOutline, outlineVariant = Green.LightOutlineVariant,
            inverseSurface = Green.LightInverseSurface, inverseOnSurface = Green.LightInverseOnSurface,
            inversePrimary = Green.LightInversePrimary, surfaceTint = Green.LightSurfaceTint
        )
        is Red -> lightColorScheme(
            primary = Red.LightPrimary, onPrimary = Red.LightOnPrimary,
            primaryContainer = Red.LightPrimaryContainer, onPrimaryContainer = Red.LightOnPrimaryContainer,
            secondary = Red.LightSecondary, onSecondary = Red.LightOnSecondary,
            secondaryContainer = Red.LightSecondaryContainer, onSecondaryContainer = Red.LightOnSecondaryContainer,
            tertiary = Red.LightTertiary, onTertiary = Red.LightOnTertiary,
            tertiaryContainer = Red.LightTertiaryContainer, onTertiaryContainer = Red.LightOnTertiaryContainer,
            error = Red.LightError, onError = Red.LightOnError,
            errorContainer = Red.LightErrorContainer, onErrorContainer = Red.LightOnErrorContainer,
            background = Red.LightSurface, onBackground = Red.LightOnSurface,
            surface = Red.LightSurface, onSurface = Red.LightOnSurface,
            surfaceVariant = Red.LightSurfaceVariant, onSurfaceVariant = Red.LightOnSurfaceVariant,
            outline = Red.LightOutline, outlineVariant = Red.LightOutlineVariant,
            inverseSurface = Red.LightInverseSurface, inverseOnSurface = Red.LightInverseOnSurface,
            inversePrimary = Red.LightInversePrimary, surfaceTint = Red.LightSurfaceTint
        )
        else -> lightColorScheme(
            primary = Pink.LightPrimary, onPrimary = Pink.LightOnPrimary,
            primaryContainer = Pink.LightPrimaryContainer, onPrimaryContainer = Pink.LightOnPrimaryContainer,
            secondary = Pink.LightSecondary, onSecondary = Pink.LightOnSecondary,
            secondaryContainer = Pink.LightSecondaryContainer, onSecondaryContainer = Pink.LightOnSecondaryContainer,
            tertiary = Pink.LightTertiary, onTertiary = Pink.LightOnTertiary,
            tertiaryContainer = Pink.LightTertiaryContainer, onTertiaryContainer = Pink.LightOnTertiaryContainer,
            error = Pink.LightError, onError = Pink.LightOnError,
            errorContainer = Pink.LightErrorContainer, onErrorContainer = Pink.LightOnErrorContainer,
            background = Pink.LightSurface, onBackground = Pink.LightOnSurface,
            surface = Pink.LightSurface, onSurface = Pink.LightOnSurface,
            surfaceVariant = Pink.LightSurfaceVariant, onSurfaceVariant = Pink.LightOnSurfaceVariant,
            outline = Pink.LightOutline, outlineVariant = Pink.LightOutlineVariant,
            inverseSurface = Pink.LightInverseSurface, inverseOnSurface = Pink.LightInverseOnSurface,
            inversePrimary = Pink.LightInversePrimary, surfaceTint = Pink.LightSurfaceTint
        )
    }
}

private fun buildDarkColorScheme(theme: Any): androidx.compose.material3.ColorScheme {
    return when (theme) {
        is Gold -> darkColorScheme(
            primary = Gold.DarkPrimary, onPrimary = Gold.DarkOnPrimary,
            primaryContainer = Gold.DarkPrimaryContainer, onPrimaryContainer = Gold.DarkOnPrimaryContainer,
            secondary = Gold.DarkSecondary, onSecondary = Gold.DarkOnSecondary,
            secondaryContainer = Gold.DarkSecondaryContainer, onSecondaryContainer = Gold.DarkOnSecondaryContainer,
            tertiary = Gold.DarkTertiary, onTertiary = Gold.DarkOnTertiary,
            tertiaryContainer = Gold.DarkTertiaryContainer, onTertiaryContainer = Gold.DarkOnTertiaryContainer,
            error = Gold.DarkError, onError = Gold.DarkOnError,
            errorContainer = Gold.DarkErrorContainer, onErrorContainer = Gold.DarkOnErrorContainer,
            background = Gold.DarkSurface, onBackground = Gold.DarkOnSurface,
            surface = Gold.DarkSurface, onSurface = Gold.DarkOnSurface,
            surfaceVariant = Gold.DarkSurfaceVariant, onSurfaceVariant = Gold.DarkOnSurfaceVariant,
            outline = Gold.DarkOutline, outlineVariant = Gold.DarkOutlineVariant,
            inverseSurface = Gold.DarkInverseSurface, inverseOnSurface = Gold.DarkInverseOnSurface,
            inversePrimary = Gold.DarkInversePrimary, surfaceTint = Gold.DarkSurfaceTint
        )
        is Blue -> darkColorScheme(
            primary = Blue.DarkPrimary, onPrimary = Blue.DarkOnPrimary,
            primaryContainer = Blue.DarkPrimaryContainer, onPrimaryContainer = Blue.DarkOnPrimaryContainer,
            secondary = Blue.DarkSecondary, onSecondary = Blue.DarkOnSecondary,
            secondaryContainer = Blue.DarkSecondaryContainer, onSecondaryContainer = Blue.DarkOnSecondaryContainer,
            tertiary = Blue.DarkTertiary, onTertiary = Blue.DarkOnTertiary,
            tertiaryContainer = Blue.DarkTertiaryContainer, onTertiaryContainer = Blue.DarkOnTertiaryContainer,
            error = Blue.DarkError, onError = Blue.DarkOnError,
            errorContainer = Blue.DarkErrorContainer, onErrorContainer = Blue.DarkOnErrorContainer,
            background = Blue.DarkSurface, onBackground = Blue.DarkOnSurface,
            surface = Blue.DarkSurface, onSurface = Blue.DarkOnSurface,
            surfaceVariant = Blue.DarkSurfaceVariant, onSurfaceVariant = Blue.DarkOnSurfaceVariant,
            outline = Blue.DarkOutline, outlineVariant = Blue.DarkOutlineVariant,
            inverseSurface = Blue.DarkInverseSurface, inverseOnSurface = Blue.DarkInverseOnSurface,
            inversePrimary = Blue.DarkInversePrimary, surfaceTint = Blue.DarkSurfaceTint
        )
        is Green -> darkColorScheme(
            primary = Green.DarkPrimary, onPrimary = Green.DarkOnPrimary,
            primaryContainer = Green.DarkPrimaryContainer, onPrimaryContainer = Green.DarkOnPrimaryContainer,
            secondary = Green.DarkSecondary, onSecondary = Green.DarkOnSecondary,
            secondaryContainer = Green.DarkSecondaryContainer, onSecondaryContainer = Green.DarkOnSecondaryContainer,
            tertiary = Green.DarkTertiary, onTertiary = Green.DarkOnTertiary,
            tertiaryContainer = Green.DarkTertiaryContainer, onTertiaryContainer = Green.DarkOnTertiaryContainer,
            error = Green.DarkError, onError = Green.DarkOnError,
            errorContainer = Green.DarkErrorContainer, onErrorContainer = Green.DarkOnErrorContainer,
            background = Green.DarkSurface, onBackground = Green.DarkOnSurface,
            surface = Green.DarkSurface, onSurface = Green.DarkOnSurface,
            surfaceVariant = Green.DarkSurfaceVariant, onSurfaceVariant = Green.DarkOnSurfaceVariant,
            outline = Green.DarkOutline, outlineVariant = Green.DarkOutlineVariant,
            inverseSurface = Green.DarkInverseSurface, inverseOnSurface = Green.DarkInverseOnSurface,
            inversePrimary = Green.DarkInversePrimary, surfaceTint = Green.DarkSurfaceTint
        )
        is Red -> darkColorScheme(
            primary = Red.DarkPrimary, onPrimary = Red.DarkOnPrimary,
            primaryContainer = Red.DarkPrimaryContainer, onPrimaryContainer = Red.DarkOnPrimaryContainer,
            secondary = Red.DarkSecondary, onSecondary = Red.DarkOnSecondary,
            secondaryContainer = Red.DarkSecondaryContainer, onSecondaryContainer = Red.DarkOnSecondaryContainer,
            tertiary = Red.DarkTertiary, onTertiary = Red.DarkOnTertiary,
            tertiaryContainer = Red.DarkTertiaryContainer, onTertiaryContainer = Red.DarkOnTertiaryContainer,
            error = Red.DarkError, onError = Red.DarkOnError,
            errorContainer = Red.DarkErrorContainer, onErrorContainer = Red.DarkOnErrorContainer,
            background = Red.DarkSurface, onBackground = Red.DarkOnSurface,
            surface = Red.DarkSurface, onSurface = Red.DarkOnSurface,
            surfaceVariant = Red.DarkSurfaceVariant, onSurfaceVariant = Red.DarkOnSurfaceVariant,
            outline = Red.DarkOutline, outlineVariant = Red.DarkOutlineVariant,
            inverseSurface = Red.DarkInverseSurface, inverseOnSurface = Red.DarkInverseOnSurface,
            inversePrimary = Red.DarkInversePrimary, surfaceTint = Red.DarkSurfaceTint
        )
        else -> darkColorScheme(
            primary = Pink.DarkPrimary, onPrimary = Pink.DarkOnPrimary,
            primaryContainer = Pink.DarkPrimaryContainer, onPrimaryContainer = Pink.DarkOnPrimaryContainer,
            secondary = Pink.DarkSecondary, onSecondary = Pink.DarkOnSecondary,
            secondaryContainer = Pink.DarkSecondaryContainer, onSecondaryContainer = Pink.DarkOnSecondaryContainer,
            tertiary = Pink.DarkTertiary, onTertiary = Pink.DarkOnTertiary,
            tertiaryContainer = Pink.DarkTertiaryContainer, onTertiaryContainer = Pink.DarkOnTertiaryContainer,
            error = Pink.DarkError, onError = Pink.DarkOnError,
            errorContainer = Pink.DarkErrorContainer, onErrorContainer = Pink.DarkOnErrorContainer,
            background = Pink.DarkSurface, onBackground = Pink.DarkOnSurface,
            surface = Pink.DarkSurface, onSurface = Pink.DarkOnSurface,
            surfaceVariant = Pink.DarkSurfaceVariant, onSurfaceVariant = Pink.DarkOnSurfaceVariant,
            outline = Pink.DarkOutline, outlineVariant = Pink.DarkOutlineVariant,
            inverseSurface = Pink.DarkInverseSurface, inverseOnSurface = Pink.DarkInverseOnSurface,
            inversePrimary = Pink.DarkInversePrimary, surfaceTint = Pink.DarkSurfaceTint
        )
    }
}

private fun getThemeObject(themeEnum: AppThemeEnum): Any {
    return when (themeEnum) {
        AppThemeEnum.GOLD -> Gold
        AppThemeEnum.BLUE -> Blue
        AppThemeEnum.GREEN -> Green
        AppThemeEnum.RED -> Red
        AppThemeEnum.PINK -> Pink
    }
}

@Suppress("DEPRECATION")
@Composable
fun KenjinxAndroidTheme(
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current

    val recomposeKey = themeRecomposeKey

    val resolvedTheme = remember(recomposeKey) { getSavedTheme(context) }
    val resolvedMode = remember(recomposeKey) { getSavedThemeMode(context) }

    val isDark = when (resolvedMode) {
        ThemeMode.DARK -> true
        ThemeMode.LIGHT -> false
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
    }

    val themeObj = remember(resolvedTheme) { getThemeObject(resolvedTheme) }

    val colorScheme = remember(isDark, resolvedTheme, recomposeKey) {
        when {
            dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
                if (isDark) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
            }
            isDark -> buildDarkColorScheme(themeObj)
            else -> buildLightColorScheme(themeObj)
        }
    }

    val shapes = remember {
        Shapes(
            extraSmall = RoundedCornerShape(4.dp),
            small = RoundedCornerShape(8.dp),
            medium = RoundedCornerShape(12.dp),
            large = RoundedCornerShape(16.dp),
            extraLarge = RoundedCornerShape(24.dp)
        )
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = Color.Transparent.toArgb()
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = !isDark
                isAppearanceLightNavigationBars = !isDark
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content,
        shapes = shapes
    )
}