package com.example.notefrais.model

object NoteFraisManager {
    private val notes = mutableListOf<NoteFrais>()
    private var nextId = 1

    fun ajouterNote(note: NoteFrais): Int {
        note.id = nextId++
        note.dateCreation = getCurrentDate()
        notes.add(note)
        return note.id
    }

    fun getNote(id: Int): NoteFrais? {
        return notes.find { it.id == id }
    }

    fun getToutesLesNotes(): List<NoteFrais> {
        return notes.toList()
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

    fun getNotesParStatut(statut: String): List<NoteFrais> {
        return if (statut == "Tous") notes.toList()
        else notes.filter { it.statut == statut }
    }

    private fun getCurrentDate(): String {
        // Retourne la date actuelle formatée
        return "01/01/2024" // Simulation
    }
}