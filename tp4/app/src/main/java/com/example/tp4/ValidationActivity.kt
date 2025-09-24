package com.example.tp4

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.tp4.databinding.ActivityValidationBinding
import com.example.tp4.model.NoteFrais

class ValidationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityValidationBinding
    private lateinit var noteFrais: NoteFrais
    private val delaisTraitement = arrayOf("24h", "48h", "1 semaine", "2 semaines")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityValidationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        recupererDonnees()
        setupViews()
        setupListeners()
        afficherRecapitulatif()
    }

    private fun recupererDonnees() {
        // Récupérer toutes les données passées depuis MainActivity
        noteFrais = NoteFrais().apply {
            nomEmploye = intent.getStringExtra("nom_employe") ?: ""
            numeroEmploye = intent.getStringExtra("numero_employe") ?: ""
            departement = intent.getStringExtra("departement") ?: ""
            typeFrais = intent.getStringExtra("type_frais") ?: ""
            montant = intent.getDoubleExtra("montant", 0.0)
            avecTVA = intent.getBooleanExtra("avec_tva", false)
            fraisRecurrent = intent.getBooleanExtra("frais_recurrent", false)
            justificatifFourni = intent.getBooleanExtra("justificatif_fourni", false)
            urgence = intent.getStringExtra("urgence") ?: "Normal"
        }
    }

    private fun setupViews() {
        // Configuration du Spinner pour les délais de traitement
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, delaisTraitement)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerDelai.adapter = adapter
        binding.spinnerDelai.setSelection(1) // 48h par défaut

        // Couleur du récapitulatif selon l'urgence
        val couleur = Color.parseColor(noteFrais.getCouleurUrgence())
        binding.layoutRecapitulatif.setBackgroundColor(couleur)
    }

    private fun setupListeners() {
        // Validation conditionnelle des commentaires selon le statut
        binding.radioGroupStatut.setOnCheckedChangeListener { _, checkedId ->
            val estRefuse = checkedId == R.id.radioBtnRefuse
            binding.editTextCommentaires.hint = if (estRefuse) {
                "Commentaires obligatoires pour un refus"
            } else {
                "Commentaires du manager (optionnel)"
            }
            validateCommentaires()
        }

        // Validation en temps réel des commentaires
        binding.editTextCommentaires.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) validateCommentaires()
        }

        // Bouton Valider - Confirmer et retourner les données
        binding.btnValider.setOnClickListener {
            if (validateForm()) {
                validerEtRetourner()
            }
        }

        // Bouton Modifier - Retourner à MainActivity pour modification
        binding.btnModifier.setOnClickListener {
            // Retourner les données pour pré-remplir le formulaire
            val resultIntent = Intent()
            resultIntent.putExtra("action", "modifier")
            resultIntent.putExtra("nom_employe", noteFrais.nomEmploye)
            resultIntent.putExtra("numero_employe", noteFrais.numeroEmploye)
            resultIntent.putExtra("departement", noteFrais.departement)
            resultIntent.putExtra("type_frais", noteFrais.typeFrais)
            resultIntent.putExtra("montant", noteFrais.montant)
            resultIntent.putExtra("avec_tva", noteFrais.avecTVA)
            resultIntent.putExtra("frais_recurrent", noteFrais.fraisRecurrent)
            resultIntent.putExtra("justificatif_fourni", noteFrais.justificatifFourni)
            resultIntent.putExtra("urgence", noteFrais.urgence)

            setResult(RESULT_OK, resultIntent)
            finish()
        }

        // Bouton Annuler - Retour sans sauvegarde
        binding.btnAnnuler.setOnClickListener {
            setResult(RESULT_CANCELED)
            finish()
        }
    }

    private fun afficherRecapitulatif() {
        // Afficher toutes les informations de la note de frais
        binding.textViewRecapNom.text = "Employé: ${noteFrais.nomEmploye}"
        binding.textViewRecapNumero.text = "N° employé: ${noteFrais.numeroEmploye}"
        binding.textViewRecapDepartement.text = "Département: ${noteFrais.departement}"
        binding.textViewRecapTypeFrais.text = "Type de frais: ${noteFrais.typeFrais}"

        // Montants avec calculs
        val montantHT = String.format("%.2f", noteFrais.montant)
        val montantTTC = String.format("%.2f", noteFrais.calculerMontantTTC())
        binding.textViewRecapMontant.text = "Montant HT: $montantHT€ / TTC: $montantTTC€"

        // Options sélectionnées
        val options = buildList {
            add("Urgence: ${noteFrais.urgence}")
            if (noteFrais.fraisRecurrent) add("Frais récurrent")
            if (noteFrais.justificatifFourni) add("Justificatif fourni")
            if (noteFrais.avecTVA) add("TVA récupérable")
        }
        binding.textViewRecapOptions.text = options.joinToString(" • ")
    }

    private fun validateCommentaires(): Boolean {
        val checkedId = binding.radioGroupStatut.checkedRadioButtonId
        val commentaires = binding.editTextCommentaires.text.toString().trim()

        // Commentaires obligatoires uniquement pour un refus
        return if (checkedId == R.id.radioBtnRefuse && commentaires.isEmpty()) {
            binding.textViewErreurCommentaires.text = "Les commentaires sont obligatoires pour un refus"
            binding.textViewErreurCommentaires.visibility = View.VISIBLE
            false
        } else {
            binding.textViewErreurCommentaires.visibility = View.GONE
            true
        }
    }

    private fun validateForm(): Boolean {
        val hasStatut = binding.radioGroupStatut.checkedRadioButtonId != -1
        val commentairesValid = validateCommentaires()

        if (!hasStatut) {
            Toast.makeText(this, "Veuillez sélectionner un statut de validation", Toast.LENGTH_SHORT).show()
            return false
        }

        return commentairesValid
    }

    private fun validerEtRetourner() {
        // Récupérer toutes les données de validation
        val statutId = binding.radioGroupStatut.checkedRadioButtonId
        val statut = when (statutId) {
            R.id.radioBtnApprouve -> "Approuvé"
            R.id.radioBtnRefuse -> "Refusé"
            R.id.radioBtnEnAttente -> "En attente"
            else -> "En attente"
        }

        val commentaires = binding.editTextCommentaires.text.toString().trim()
        val prioritaire = binding.checkBoxPrioritaire.isChecked
        val delai = binding.spinnerDelai.selectedItem.toString()

        // Créer l'intent de retour avec tous les résultats
        val resultIntent = Intent()
        resultIntent.putExtra("statut_validation", statut)
        resultIntent.putExtra("commentaires_manager", commentaires)
        resultIntent.putExtra("remboursement_prioritaire", prioritaire)
        resultIntent.putExtra("delai_traitement", delai)

        setResult(RESULT_OK, resultIntent)
        finish()
    }

    // Gérer le bouton retour système avec confirmation
    override fun onBackPressed() {
        AlertDialog.Builder(this)
            .setTitle("Confirmation")
            .setMessage("Quitter la validation sans sauvegarder ?")
            .setPositiveButton("Oui") { _, _ ->
                setResult(RESULT_CANCELED)
                super.onBackPressed()
            }
            .setNegativeButton("Non", null)
            .show()
    }
}