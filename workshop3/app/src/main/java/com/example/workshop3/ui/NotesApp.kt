package com.example.workshop3.ui

import androidx.compose.runtime.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.workshop3.data.Note
import com.example.workshop3.ui.screens.EcranListeNotes
import com.example.workshop3.ui.screens.EcranDetailNote
import com.example.workshop3.ui.screens.EcranAjouterNote
import com.example.workshop3.ui.screens.EcranErreur

@Composable
fun NotesApp() {
    val navController = rememberNavController()

    // État global des notes partagé entre les écrans
    var notes by remember {
        mutableStateOf(
            listOf(
                Note(1, "Ma première note", "Voici le contenu de ma première note avec Jetpack Compose"),
                Note(2, "Liste de courses", "Lait\nPain\nOeufs\nFromage\nPommes"),
                Note(3, "Idées projet", "Application mobile avec Compose\nSite web avec React\nAPI REST avec Kotlin"),
                Note(4, "Rappels", "Rendez-vous médecin lundi 14h\nAppeler maman\nFinir le projet Compose")
            )
        )
    }

    var prochainId by remember { mutableStateOf(5) }

    NavHost(navController, startDestination = "liste") {
        composable("liste") {
            EcranListeNotes(
                notes = notes,
                onNoteClick = { noteId ->
                    navController.navigate("detail/$noteId")
                },
                onAjouterNote = {
                    navController.navigate("ajouter")
                }
            )
        }

        composable("detail/{noteId}") { backStackEntry ->
            val noteId = backStackEntry.arguments?.getString("noteId")?.toIntOrNull()
            val note = notes.find { it.id == noteId }

            if (note != null) {
                EcranDetailNote(
                    note = note,
                    onRetour = { navController.popBackStack() }
                )
            } else {
                EcranErreur(onRetour = { navController.popBackStack() })
            }
        }

        composable("ajouter") {
            EcranAjouterNote(
                onRetour = { navController.popBackStack() },
                onSauvegarder = { titre, contenu ->
                    val nouvelleNote = Note(
                        id = prochainId++,
                        titre = titre.ifEmpty { "Note sans titre" },
                        contenu = contenu
                    )
                    notes = listOf(nouvelleNote) + notes
                    navController.popBackStack()
                }
            )
        }
    }
}