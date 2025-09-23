package com.example.notefrais.model

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
    // Nouveaux champs pour validation
    var statut: String = "En attente", // "Approuvé", "Refusé", "En attente"
    var commentairesManager: String = "",
    var remboursementPrioritaire: Boolean = false,
    var delaiTraitement: String = "48h",
    var dateCreation: String = "",
    var dateValidation: String = ""
) {
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

    fun getPourcentageBudget(): Int {
        // Simulation : budget mensuel de 1000€ par département
        return ((montant / 1000.0) * 100).toInt().coerceAtMost(100)
    }
}