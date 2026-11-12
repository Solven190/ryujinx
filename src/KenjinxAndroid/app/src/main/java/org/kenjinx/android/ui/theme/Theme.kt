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
        .getString(KEY_THEME, AppThemeEnum.MINT_FOREST.name) ?: AppThemeEnum.MINT_FOREST.name
    return try {
        AppThemeEnum.valueOf(name)
    } catch (_: Exception) {
        AppThemeEnum.MINT_FOREST
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
        is MintForest -> lightColorScheme(
            primary = MintForest.LightPrimary, onPrimary = MintForest.LightOnPrimary,
            primaryContainer = MintForest.LightPrimaryContainer, onPrimaryContainer = MintForest.LightOnPrimaryContainer,
            secondary = MintForest.LightSecondary, onSecondary = MintForest.LightOnSecondary,
            secondaryContainer = MintForest.LightSecondaryContainer, onSecondaryContainer = MintForest.LightOnSecondaryContainer,
            tertiary = MintForest.LightTertiary, onTertiary = MintForest.LightOnTertiary,
            tertiaryContainer = MintForest.LightTertiaryContainer, onTertiaryContainer = MintForest.LightOnTertiaryContainer,
            error = MintForest.LightError, onError = MintForest.LightOnError,
            errorContainer = MintForest.LightErrorContainer, onErrorContainer = MintForest.LightOnErrorContainer,
            background = MintForest.LightSurface, onBackground = MintForest.LightOnSurface,
            surface = Color.White, onSurface = MintForest.LightOnSurface,
            surfaceVariant = MintForest.LightSurfaceVariant, onSurfaceVariant = MintForest.LightOnSurfaceVariant,
            outline = MintForest.LightOutline, outlineVariant = MintForest.LightOutlineVariant,
            inverseSurface = MintForest.LightInverseSurface, inverseOnSurface = MintForest.LightInverseOnSurface,
            inversePrimary = MintForest.LightInversePrimary, surfaceTint = MintForest.LightSurfaceTint
        )
        is OceanBlue -> lightColorScheme(
            primary = OceanBlue.LightPrimary, onPrimary = OceanBlue.LightOnPrimary,
            primaryContainer = OceanBlue.LightPrimaryContainer, onPrimaryContainer = OceanBlue.LightOnPrimaryContainer,
            secondary = OceanBlue.LightSecondary, onSecondary = OceanBlue.LightOnSecondary,
            secondaryContainer = OceanBlue.LightSecondaryContainer, onSecondaryContainer = OceanBlue.LightOnSecondaryContainer,
            tertiary = OceanBlue.LightTertiary, onTertiary = OceanBlue.LightOnTertiary,
            tertiaryContainer = OceanBlue.LightTertiaryContainer, onTertiaryContainer = OceanBlue.LightOnTertiaryContainer,
            error = OceanBlue.LightError, onError = OceanBlue.LightOnError,
            errorContainer = OceanBlue.LightErrorContainer, onErrorContainer = OceanBlue.LightOnErrorContainer,
            background = OceanBlue.LightSurface, onBackground = OceanBlue.LightOnSurface,
            surface = Color.White, onSurface = OceanBlue.LightOnSurface,
            surfaceVariant = OceanBlue.LightSurfaceVariant, onSurfaceVariant = OceanBlue.LightOnSurfaceVariant,
            outline = OceanBlue.LightOutline, outlineVariant = OceanBlue.LightOutlineVariant,
            inverseSurface = OceanBlue.LightInverseSurface, inverseOnSurface = OceanBlue.LightInverseOnSurface,
            inversePrimary = OceanBlue.LightInversePrimary, surfaceTint = OceanBlue.LightSurfaceTint
        )
        is VelvetViolet -> lightColorScheme(
            primary = VelvetViolet.LightPrimary, onPrimary = VelvetViolet.LightOnPrimary,
            primaryContainer = VelvetViolet.LightPrimaryContainer, onPrimaryContainer = VelvetViolet.LightOnPrimaryContainer,
            secondary = VelvetViolet.LightSecondary, onSecondary = VelvetViolet.LightOnSecondary,
            secondaryContainer = VelvetViolet.LightSecondaryContainer, onSecondaryContainer = VelvetViolet.LightOnSecondaryContainer,
            tertiary = VelvetViolet.LightTertiary, onTertiary = VelvetViolet.LightOnTertiary,
            tertiaryContainer = VelvetViolet.LightTertiaryContainer, onTertiaryContainer = VelvetViolet.LightOnTertiaryContainer,
            error = VelvetViolet.LightError, onError = VelvetViolet.LightOnError,
            errorContainer = VelvetViolet.LightErrorContainer, onErrorContainer = VelvetViolet.LightOnErrorContainer,
            background = VelvetViolet.LightSurface, onBackground = VelvetViolet.LightOnSurface,
            surface = Color.White, onSurface = VelvetViolet.LightOnSurface,
            surfaceVariant = VelvetViolet.LightSurfaceVariant, onSurfaceVariant = VelvetViolet.LightOnSurfaceVariant,
            outline = VelvetViolet.LightOutline, outlineVariant = VelvetViolet.LightOutlineVariant,
            inverseSurface = VelvetViolet.LightInverseSurface, inverseOnSurface = VelvetViolet.LightInverseOnSurface,
            inversePrimary = VelvetViolet.LightInversePrimary, surfaceTint = VelvetViolet.LightSurfaceTint
        )
        is SunsetAmber -> lightColorScheme(
            primary = SunsetAmber.LightPrimary, onPrimary = SunsetAmber.LightOnPrimary,
            primaryContainer = SunsetAmber.LightPrimaryContainer, onPrimaryContainer = SunsetAmber.LightOnPrimaryContainer,
            secondary = SunsetAmber.LightSecondary, onSecondary = SunsetAmber.LightOnSecondary,
            secondaryContainer = SunsetAmber.LightSecondaryContainer, onSecondaryContainer = SunsetAmber.LightOnSecondaryContainer,
            tertiary = SunsetAmber.LightTertiary, onTertiary = SunsetAmber.LightOnTertiary,
            tertiaryContainer = SunsetAmber.LightTertiaryContainer, onTertiaryContainer = SunsetAmber.LightOnTertiaryContainer,
            error = SunsetAmber.LightError, onError = SunsetAmber.LightOnError,
            errorContainer = SunsetAmber.LightErrorContainer, onErrorContainer = SunsetAmber.LightOnErrorContainer,
            background = SunsetAmber.LightSurface, onBackground = SunsetAmber.LightOnSurface,
            surface = Color.White, onSurface = SunsetAmber.LightOnSurface,
            surfaceVariant = SunsetAmber.LightSurfaceVariant, onSurfaceVariant = SunsetAmber.LightOnSurfaceVariant,
            outline = SunsetAmber.LightOutline, outlineVariant = SunsetAmber.LightOutlineVariant,
            inverseSurface = SunsetAmber.LightInverseSurface, inverseOnSurface = SunsetAmber.LightInverseOnSurface,
            inversePrimary = SunsetAmber.LightInversePrimary, surfaceTint = SunsetAmber.LightSurfaceTint
        )
        else -> lightColorScheme(
            primary = RoseBloom.LightPrimary, onPrimary = RoseBloom.LightOnPrimary,
            primaryContainer = RoseBloom.LightPrimaryContainer, onPrimaryContainer = RoseBloom.LightOnPrimaryContainer,
            secondary = RoseBloom.LightSecondary, onSecondary = RoseBloom.LightOnSecondary,
            secondaryContainer = RoseBloom.LightSecondaryContainer, onSecondaryContainer = RoseBloom.LightOnSecondaryContainer,
            tertiary = RoseBloom.LightTertiary, onTertiary = RoseBloom.LightOnTertiary,
            tertiaryContainer = RoseBloom.LightTertiaryContainer, onTertiaryContainer = RoseBloom.LightOnTertiaryContainer,
            error = RoseBloom.LightError, onError = RoseBloom.LightOnError,
            errorContainer = RoseBloom.LightErrorContainer, onErrorContainer = RoseBloom.LightOnErrorContainer,
            background = RoseBloom.LightSurface, onBackground = RoseBloom.LightOnSurface,
            surface = Color.White, onSurface = RoseBloom.LightOnSurface,
            surfaceVariant = RoseBloom.LightSurfaceVariant, onSurfaceVariant = RoseBloom.LightOnSurfaceVariant,
            outline = RoseBloom.LightOutline, outlineVariant = RoseBloom.LightOutlineVariant,
            inverseSurface = RoseBloom.LightInverseSurface, inverseOnSurface = RoseBloom.LightInverseOnSurface,
            inversePrimary = RoseBloom.LightInversePrimary, surfaceTint = RoseBloom.LightSurfaceTint
        )
    }
}

private fun buildDarkColorScheme(theme: Any): androidx.compose.material3.ColorScheme {
    return when (theme) {
        is MintForest -> darkColorScheme(
            primary = MintForest.DarkPrimary, onPrimary = MintForest.DarkOnPrimary,
            primaryContainer = MintForest.DarkPrimaryContainer, onPrimaryContainer = MintForest.DarkOnPrimaryContainer,
            secondary = MintForest.DarkSecondary, onSecondary = MintForest.DarkOnSecondary,
            secondaryContainer = MintForest.DarkSecondaryContainer, onSecondaryContainer = MintForest.DarkOnSecondaryContainer,
            tertiary = MintForest.DarkTertiary, onTertiary = MintForest.DarkOnTertiary,
            tertiaryContainer = MintForest.DarkTertiaryContainer, onTertiaryContainer = MintForest.DarkOnTertiaryContainer,
            error = MintForest.DarkError, onError = MintForest.DarkOnError,
            errorContainer = MintForest.DarkErrorContainer, onErrorContainer = MintForest.DarkOnErrorContainer,
            background = MintForest.DarkSurface, onBackground = MintForest.DarkOnSurface,
            surface = MintForest.DarkSurface, onSurface = MintForest.DarkOnSurface,
            surfaceVariant = MintForest.DarkSurfaceVariant, onSurfaceVariant = MintForest.DarkOnSurfaceVariant,
            outline = MintForest.DarkOutline, outlineVariant = MintForest.DarkOutlineVariant,
            inverseSurface = MintForest.DarkInverseSurface, inverseOnSurface = MintForest.DarkInverseOnSurface,
            inversePrimary = MintForest.DarkInversePrimary, surfaceTint = MintForest.DarkSurfaceTint
        )
        is OceanBlue -> darkColorScheme(
            primary = OceanBlue.DarkPrimary, onPrimary = OceanBlue.DarkOnPrimary,
            primaryContainer = OceanBlue.DarkPrimaryContainer, onPrimaryContainer = OceanBlue.DarkOnPrimaryContainer,
            secondary = OceanBlue.DarkSecondary, onSecondary = OceanBlue.DarkOnSecondary,
            secondaryContainer = OceanBlue.DarkSecondaryContainer, onSecondaryContainer = OceanBlue.DarkOnSecondaryContainer,
            tertiary = OceanBlue.DarkTertiary, onTertiary = OceanBlue.DarkOnTertiary,
            tertiaryContainer = OceanBlue.DarkTertiaryContainer, onTertiaryContainer = OceanBlue.DarkOnTertiaryContainer,
            error = OceanBlue.DarkError, onError = OceanBlue.DarkOnError,
            errorContainer = OceanBlue.DarkErrorContainer, onErrorContainer = OceanBlue.DarkOnErrorContainer,
            background = OceanBlue.DarkSurface, onBackground = OceanBlue.DarkOnSurface,
            surface = OceanBlue.DarkSurface, onSurface = OceanBlue.DarkOnSurface,
            surfaceVariant = OceanBlue.DarkSurfaceVariant, onSurfaceVariant = OceanBlue.DarkOnSurfaceVariant,
            outline = OceanBlue.DarkOutline, outlineVariant = OceanBlue.DarkOutlineVariant,
            inverseSurface = OceanBlue.DarkInverseSurface, inverseOnSurface = OceanBlue.DarkInverseOnSurface,
            inversePrimary = OceanBlue.DarkInversePrimary, surfaceTint = OceanBlue.DarkSurfaceTint
        )
        is VelvetViolet -> darkColorScheme(
            primary = VelvetViolet.DarkPrimary, onPrimary = VelvetViolet.DarkOnPrimary,
            primaryContainer = VelvetViolet.DarkPrimaryContainer, onPrimaryContainer = VelvetViolet.DarkOnPrimaryContainer,
            secondary = VelvetViolet.DarkSecondary, onSecondary = VelvetViolet.DarkOnSecondary,
            secondaryContainer = VelvetViolet.DarkSecondaryContainer, onSecondaryContainer = VelvetViolet.DarkOnSecondaryContainer,
            tertiary = VelvetViolet.DarkTertiary, onTertiary = VelvetViolet.DarkOnTertiary,
            tertiaryContainer = VelvetViolet.DarkTertiaryContainer, onTertiaryContainer = VelvetViolet.DarkOnTertiaryContainer,
            error = VelvetViolet.DarkError, onError = VelvetViolet.DarkOnError,
            errorContainer = VelvetViolet.DarkErrorContainer, onErrorContainer = VelvetViolet.DarkOnErrorContainer,
            background = VelvetViolet.DarkSurface, onBackground = VelvetViolet.DarkOnSurface,
            surface = VelvetViolet.DarkSurface, onSurface = VelvetViolet.DarkOnSurface,
            surfaceVariant = VelvetViolet.DarkSurfaceVariant, onSurfaceVariant = VelvetViolet.DarkOnSurfaceVariant,
            outline = VelvetViolet.DarkOutline, outlineVariant = VelvetViolet.DarkOutlineVariant,
            inverseSurface = VelvetViolet.DarkInverseSurface, inverseOnSurface = VelvetViolet.DarkInverseOnSurface,
            inversePrimary = VelvetViolet.DarkInversePrimary, surfaceTint = VelvetViolet.DarkSurfaceTint
        )
        is SunsetAmber -> darkColorScheme(
            primary = SunsetAmber.DarkPrimary, onPrimary = SunsetAmber.DarkOnPrimary,
            primaryContainer = SunsetAmber.DarkPrimaryContainer, onPrimaryContainer = SunsetAmber.DarkOnPrimaryContainer,
            secondary = SunsetAmber.DarkSecondary, onSecondary = SunsetAmber.DarkOnSecondary,
            secondaryContainer = SunsetAmber.DarkSecondaryContainer, onSecondaryContainer = SunsetAmber.DarkOnSecondaryContainer,
            tertiary = SunsetAmber.DarkTertiary, onTertiary = SunsetAmber.DarkOnTertiary,
            tertiaryContainer = SunsetAmber.DarkTertiaryContainer, onTertiaryContainer = SunsetAmber.DarkOnTertiaryContainer,
            error = SunsetAmber.DarkError, onError = SunsetAmber.DarkOnError,
            errorContainer = SunsetAmber.DarkErrorContainer, onErrorContainer = SunsetAmber.DarkOnErrorContainer,
            background = SunsetAmber.DarkSurface, onBackground = SunsetAmber.DarkOnSurface,
            surface = SunsetAmber.DarkSurface, onSurface = SunsetAmber.DarkOnSurface,
            surfaceVariant = SunsetAmber.DarkSurfaceVariant, onSurfaceVariant = SunsetAmber.DarkOnSurfaceVariant,
            outline = SunsetAmber.DarkOutline, outlineVariant = SunsetAmber.DarkOutlineVariant,
            inverseSurface = SunsetAmber.DarkInverseSurface, inverseOnSurface = SunsetAmber.DarkInverseOnSurface,
            inversePrimary = SunsetAmber.DarkInversePrimary, surfaceTint = SunsetAmber.DarkSurfaceTint
        )
        else -> darkColorScheme(
            primary = RoseBloom.DarkPrimary, onPrimary = RoseBloom.DarkOnPrimary,
            primaryContainer = RoseBloom.DarkPrimaryContainer, onPrimaryContainer = RoseBloom.DarkOnPrimaryContainer,
            secondary = RoseBloom.DarkSecondary, onSecondary = RoseBloom.DarkOnSecondary,
            secondaryContainer = RoseBloom.DarkSecondaryContainer, onSecondaryContainer = RoseBloom.DarkOnSecondaryContainer,
            tertiary = RoseBloom.DarkTertiary, onTertiary = RoseBloom.DarkOnTertiary,
            tertiaryContainer = RoseBloom.DarkTertiaryContainer, onTertiaryContainer = RoseBloom.DarkOnTertiaryContainer,
            error = RoseBloom.DarkError, onError = RoseBloom.DarkOnError,
            errorContainer = RoseBloom.DarkErrorContainer, onErrorContainer = RoseBloom.DarkOnErrorContainer,
            background = RoseBloom.DarkSurface, onBackground = RoseBloom.DarkOnSurface,
            surface = RoseBloom.DarkSurface, onSurface = RoseBloom.DarkOnSurface,
            surfaceVariant = RoseBloom.DarkSurfaceVariant, onSurfaceVariant = RoseBloom.DarkOnSurfaceVariant,
            outline = RoseBloom.DarkOutline, outlineVariant = RoseBloom.DarkOutlineVariant,
            inverseSurface = RoseBloom.DarkInverseSurface, inverseOnSurface = RoseBloom.DarkInverseOnSurface,
            inversePrimary = RoseBloom.DarkInversePrimary, surfaceTint = RoseBloom.DarkSurfaceTint
        )
    }
}

private fun getThemeObject(themeEnum: AppThemeEnum): Any {
    return when (themeEnum) {
        AppThemeEnum.MINT_FOREST -> MintForest
        AppThemeEnum.OCEAN_BLUE -> OceanBlue
        AppThemeEnum.VELVET_VIOLET -> VelvetViolet
        AppThemeEnum.SUNSET_AMBER -> SunsetAmber
        AppThemeEnum.ROSE_BLOOM -> RoseBloom
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