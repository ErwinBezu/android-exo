package com.example.tp4.model

import java.text.SimpleDateFormat
import java.util.*

object NoteFraisManager {
    // Liste des notes de frais stockées en mémoire
    private val notes = mutableListOf<NoteFrais>()
    private var nextId = 1

    init {
        // Générer quelques données de démonstration
        genererDonneesDemonstration()
    }

    fun ajouterNote(note: NoteFrais): Int {
        // Assigner un ID unique et la date de création
        note.id = nextId++
        note.dateCreation = getCurrentDate()
        notes.add(note)
        return note.id
    }

    fun getNote(id: Int): NoteFrais? {
        return notes.find { it.id == id }
    }

    fun getToutesLesNotes(): List<NoteFrais> {
        // Retourner une copie de la liste pour éviter les modifications externes
        return notes.toList()
    }

    fun updateNote(note: NoteFrais) {
        val index = notes.indexOfFirst { it.id == note.id }
        if (index != -1) {
            // Mettre à jour la date de validation si le statut change
            if (notes[index].statut != note.statut && note.statut != "En attente") {
                note.dateValidation = getCurrentDate()
            }
            notes[index] = note
        }
    }

    fun supprimerNote(id: Int): Boolean {
        return notes.removeIf { it.id == id }
    }

    fun getNotesParStatut(statut: String): List<NoteFrais> {
        return if (statut == "Tous") {
            notes.toList()
        } else {
            notes.filter { it.statut == statut }
        }
    }

    private fun getCurrentDate(): String {
        return SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
    }

    private fun genererDonneesDemonstration() {
        // Créer quelques notes d'exemple pour tester l'application
        val exemples = listOf(
            NoteFrais().apply {
                nomEmploye = "Jean Dupont"
                numeroEmploye = "12345"
                departement = "Commercial"
                typeFrais = "Transport"
                montant = 150.0
                statut = "Approuvé"
                commentairesManager = "Frais de déplacement client validé"
            },
            NoteFrais().apply {
                nomEmploye = "Marie Martin"
                numeroEmploye = "67890"
                departement = "Marketing"
                typeFrais = "Repas"
                montant = 75.0
                statut = "En attente"
            },
            NoteFrais().apply {
                nomEmploye = "Paul Durand"
                numeroEmploye = "54321"
                departement = "IT"
                typeFrais = "Matériel"
                montant = 200.0
                avecTVA = true
                statut = "Refusé"
                commentairesManager = "Matériel non conforme aux standards"
            }
        )

        exemples.forEach { ajouterNote(it) }
    }
}