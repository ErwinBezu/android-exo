package com.example.tp4.model

data class NoteFrais(
    var id: Int = 0,
    var nomEmploye: String = "",
    var numeroEmploye: String = "",
    var departement: String = "",
    var typeFrais: String = "",
    var montant: Double = 10.0,
    var avecTVA: Boolean = false,
    var fraisRecurrent: Boolean = false,
    var justificatifFourni: Boolean = false,
    var urgence: String = "Normal",
    // Nouveaux champs pour la validation
    var statut: String = "En attente",
    var commentairesManager: String = "",
    var remboursementPrioritaire: Boolean = false,
    var delaiTraitement: String = "48h",
    var dateCreation: String = "",
    var dateValidation: String = ""
) {
    // Méthodes de validation existantes du TP2
    fun isNomValide(): Boolean {
        val mots = nomEmploye.trim().split("\\s+".toRegex())
        return mots.size >= 2 && mots.all { it.isNotEmpty() }
    }

    fun isNumeroValide(): Boolean {
        return numeroEmploye.matches("\\d{5}".toRegex())
    }

    fun calculerMontantTTC(): Double {
        return if (avecTVA) montant * 1.20 else montant
    }

    fun getCouleurUrgence(): String {
        return when (urgence) {
            "Normal" -> "#2196F3"
            "Urgent" -> "#FF9800"
            "Très urgent" -> "#F44336"
            else -> "#2196F3"
        }
    }

    // Nouvelles méthodes pour la validation
    fun getCouleurStatut(): String {
        return when (statut) {
            "Approuvé" -> "#4CAF50"
            "Refusé" -> "#F44336"
            "En attente" -> "#FF9800"
            else -> "#9E9E9E"
        }
    }

    fun genererRecapitulatifValidation(): String {
        return buildString {
            appendLine("=== RÉCAPITULATIF VALIDATION ===")
            appendLine("Employé: $nomEmploye ($numeroEmploye)")
            appendLine("Département: $departement")
            appendLine("Type: $typeFrais")
            appendLine("Montant HT: ${String.format("%.2f", montant)}€")
            appendLine("Montant TTC: ${String.format("%.2f", calculerMontantTTC())}€")
            appendLine("Urgence: $urgence")
            if (fraisRecurrent) appendLine("Frais récurrent")
            if (justificatifFourni) appendLine("Justificatif fourni")
            appendLine("Date: $dateCreation")
        }
    }

    fun isValide(): Boolean {
        return isNomValide() && isNumeroValide() && departement.isNotEmpty()
    }
}