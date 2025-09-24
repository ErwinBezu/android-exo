package com.example.notefrais.model

import androidx.compose.runtime.mutableStateListOf
import java.text.SimpleDateFormat
import java.util.*

object NoteFraisManager {
    private val notes = mutableStateListOf<NoteFrais>() // Changement pour Compose
    private var nextId = 1

    init {
        // Générer quelques données de démonstration
        genererDonneesDemonstration()
    }

    fun getToutesLesNotes(): List<NoteFrais> = notes.toList()

    fun getNote(id: Int): NoteFrais? = notes.find { it.id == id }

    fun ajouterNote(note: NoteFrais): Int {
        note.id = nextId++
        note.dateCreation = getCurrentDate()
        notes.add(note)
        return note.id
    }

    fun updateNote(note: NoteFrais) {
        val index = notes.indexOfFirst { it.id == note.id }
        if (index != -1) {
            notes[index] = note
        }
    }

    fun supprimerNote(id: Int): Boolean {
        return notes.removeIf { it.id == id }
    }

    private fun genererDonneesDemonstration() {
        // Générer 5-6 notes d'exemple simples
        repeat(6) { index ->
            notes.add(NoteFrais().apply {
                id = index + 1
                nomEmploye = "Employé ${('A' + index)}"
                numeroEmploye = "1000${index + 1}"
                departement = listOf("Commercial", "IT", "RH")[index % 3]
                typeFrais = listOf("Transport", "Repas", "Matériel")[index % 3]
                montant = listOf(50.0, 120.0, 200.0, 75.0, 300.0, 45.0)[index]
                statut = listOf("Approuvé", "En attente", "Refusé")[index % 3]
                dateCreation = "0${index + 1}/01/2024"
                urgence = if (index % 2 == 0) "Normal" else "Urgent"
            })
        }
    }

    private fun getCurrentDate(): String {
        return java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault())
            .format(java.util.Date())
    }
}