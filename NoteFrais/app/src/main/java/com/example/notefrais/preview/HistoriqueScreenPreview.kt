package com.example.notefrais.preview

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.notefrais.screens.HistoriqueScreen
import com.example.notefrais.ui.theme.NoteFraisTheme

@Preview(showBackground = true)
@Composable
fun HistoriqueScreenPreview() {
    NoteFraisTheme {
        HistoriqueScreen(
            onBackClick = {},
            onNouvelleNoteClick = {},
            onNoteClick = {}
        )
    }
}