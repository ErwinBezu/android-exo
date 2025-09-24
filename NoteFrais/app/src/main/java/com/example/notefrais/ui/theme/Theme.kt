package com.example.notefrais.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val PrimaryColor = Color(0xFF1976D2)
val SecondaryColor = Color(0xFF2196F3)
val ErrorColor = Color(0xFFF44336)

val StatusApproved = Color(0xFF4CAF50)
val StatusRejected = Color(0xFFF44336)
val StatusPending = Color(0xFFFF9800)

val UrgenceNormal = Color(0xFF2196F3)
val UrgenceUrgent = Color(0xFFFF9800)
val UrgenceVeryUrgent = Color(0xFFF44336)

@Composable
fun getStatutColor(statut: String): Color {
    return when (statut) {
        "Approuvé" -> StatusApproved
        "Refusé" -> StatusRejected
        "En attente" -> StatusPending
        else -> MaterialTheme.colorScheme.outline
    }
}

@Composable
fun getUrgenceColor(urgence: String): Color {
    return when (urgence) {
        "Normal" -> UrgenceNormal
        "Urgent" -> UrgenceUrgent
        "Très urgent" -> UrgenceVeryUrgent
        else -> UrgenceNormal
    }
}

@Composable
fun NoteFraisTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        darkColorScheme(
            primary = Color(0xFF90CAF9),
            secondary = Color(0xFF64B5F6),
            tertiary = Color(0xFF4DD0E1),
            background = Color(0xFF1C1B1F),
            surface = Color(0xFF1C1B1F),
            error = ErrorColor
        )
    } else {
        lightColorScheme(
            primary = PrimaryColor,
            secondary = SecondaryColor,
            tertiary = Color(0xFF03DAC5),
            background = Color(0xFFFFFBFE),
            surface = Color(0xFFFFFBFE),
            error = ErrorColor
        )
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}