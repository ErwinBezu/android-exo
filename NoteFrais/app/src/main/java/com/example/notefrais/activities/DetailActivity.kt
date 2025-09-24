package com.example.notefrais.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import com.example.notefrais.model.NoteFraisManager
import com.example.notefrais.screens.DetailScreen
import com.example.notefrais.ui.theme.NoteFraisTheme

class DetailActivity : ComponentActivity() {

    private var noteId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        noteId = intent.getIntExtra("note_id", -1)

        if (noteId == -1) {
            finish()
            return
        }

        setContent {
            NoteFraisTheme {
                val note = NoteFraisManager.getNote(noteId)

                if (note == null) {
                    LaunchedEffect(Unit) {
                        finish()
                    }
                } else {
                    DetailScreen(
                        note = note,
                        onBackClick = {
                            val intent = Intent(this, HistoriqueActivity::class.java)
                            startActivity(intent)
                        },
                        onModifierClick = {
                            val resultIntent = Intent()
                            resultIntent.putExtra("action", "modifie")
                            resultIntent.putExtra("nom_employe", note.nomEmploye)
                            resultIntent.putExtra("numero_employe", note.numeroEmploye)
                            resultIntent.putExtra("departement", note.departement)
                            resultIntent.putExtra("type_frais", note.typeFrais)
                            resultIntent.putExtra("montant", note.montant)
                            resultIntent.putExtra("avec_tva", note.avecTVA)
                            resultIntent.putExtra("frais_recurrent", note.fraisRecurrent)
                            resultIntent.putExtra("justificatif_fourni", note.justificatifFourni)
                            resultIntent.putExtra("urgence", note.urgence)

                            setResult(RESULT_OK, resultIntent)
                            finish()
                        },
                        onSupprimerClick = {
                            NoteFraisManager.supprimerNote(noteId)

                            val resultIntent = Intent()
                            resultIntent.putExtra("action", "supprime")
                            resultIntent.putExtra("message", "Note #$noteId supprimée avec succès")
                            setResult(RESULT_OK, resultIntent)
                            finish()
                        }
                    )
                }
            }
        }
    }
}