package com.example.notefrais.preview

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.notefrais.model.NoteFrais
import com.example.notefrais.screens.DetailScreen
import com.example.notefrais.ui.theme.NoteFraisTheme

@Preview(showBackground = true)
@Composable
fun DetailScreenPreview() {
    NoteFraisTheme {
        val sampleNote = NoteFrais().apply {
            id = 1
            nomEmploye = "Jean Dupont"
            numeroEmploye = "12345"
            departement = "Commercial"
            typeFrais = "Transport"
            montant = 150.0
            avecTVA = true
            fraisRecurrent = false
            justificatifFourni = true
            urgence = "Urgent"
            statut = "En attente"
            commentairesManager = "En cours de vérification"
            dateCreation = "15/01/2024"
            delaiTraitement = "48h"
        }

        DetailScreen(
            note = sampleNote,
            onBackClick = {},
            onModifierClick = {},
            onSupprimerClick = {}
        )
    }
}