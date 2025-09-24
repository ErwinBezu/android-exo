package com.example.tp4

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.tp4.databinding.ActivityMainBinding
import com.example.tp4.model.NoteFrais
import com.example.tp4.model.NoteFraisManager

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val noteFrais = NoteFrais()

    // Codes de requête pour les activités
    companion object {
        const val REQUEST_CODE_VALIDATION = 1001
    }

    private val typesFrais = arrayOf("Transport", "Hébergement", "Repas", "Matériel", "Formation")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupViews()
        setupListeners()
        updateNote()
    }

    private fun setupViews() {
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configuration du Spinner
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, typesFrais)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerTypeFrais.adapter = adapter

        // Configuration initiale
        binding.notePreview.setBackgroundColor(Color.parseColor(noteFrais.getCouleurUrgence()))
        binding.progressBarBudget.max = 100
    }

    private fun setupListeners() {
        // TextWatcher pour le nom employé
        binding.editTextNom.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                noteFrais.nomEmploye = s.toString()
                validateNom()
                updateNote()
            }
        })

        // TextWatcher pour le numéro employé
        binding.editTextNumero.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                noteFrais.numeroEmploye = s.toString()
                validateNumero()
                updateNote()
            }
        })

        // RadioGroup pour les départements
        binding.radioGroupDepartement.setOnCheckedChangeListener { _, checkedId ->
            noteFrais.departement = when (checkedId) {
                R.id.radioBtnCommercial -> "Commercial"
                R.id.radioBtnMarketing -> "Marketing"
                R.id.radioBtnIT -> "IT"
                R.id.radioBtnRH -> "RH"
                else -> ""
            }
            updateNote()
        }

        // Spinner pour le type de frais
        binding.spinnerTypeFrais.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                noteFrais.typeFrais = typesFrais[position]
                updateNote()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        // SeekBar pour le montant
        binding.seekBarMontant.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                noteFrais.montant = 10.0 + progress // De 10€ à 500€
                binding.textViewMontant.text = "Montant : ${noteFrais.montant.toInt()}€"
                updateNote()
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        // CheckBox et Switch pour les options
        binding.checkBoxRecurrent.setOnCheckedChangeListener { _, isChecked ->
            noteFrais.fraisRecurrent = isChecked
            updateNote()
        }

        binding.switchTVA.setOnCheckedChangeListener { _, isChecked ->
            noteFrais.avecTVA = isChecked
            updateNote()
        }

        binding.checkBoxJustificatif.setOnCheckedChangeListener { _, isChecked ->
            noteFrais.justificatifFourni = isChecked
            updateNote()
        }

        // RadioGroup pour l'urgence
        binding.radioGroupUrgence.setOnCheckedChangeListener { _, checkedId ->
            noteFrais.urgence = when (checkedId) {
                R.id.radioBtnNormal -> "Normal"
                R.id.radioBtnUrgent -> "Urgent"
                R.id.radioBtnTresUrgent -> "Très urgent"
                else -> "Normal"
            }
            binding.notePreview.setBackgroundColor(Color.parseColor(noteFrais.getCouleurUrgence()))
        }

        // Boutons d'action
        binding.btnCalculer.setOnClickListener {
            val montantTTC = noteFrais.calculerMontantTTC()
            val message = "Montant calculé: ${String.format("%.2f", montantTTC)}€"
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }

        // Bouton Soumettre - Navigation vers ValidationActivity
        binding.btnSoumettre.setOnClickListener {
            if (validateForm()) {
                // Créer l'intent pour ValidationActivity
                val intent = Intent(this, ValidationActivity::class.java)

                // Passer toutes les données de la note de frais
                intent.putExtra("nom_employe", noteFrais.nomEmploye)
                intent.putExtra("numero_employe", noteFrais.numeroEmploye)
                intent.putExtra("departement", noteFrais.departement)
                intent.putExtra("type_frais", noteFrais.typeFrais)
                intent.putExtra("montant", noteFrais.montant)
                intent.putExtra("avec_tva", noteFrais.avecTVA)
                intent.putExtra("frais_recurrent", noteFrais.fraisRecurrent)
                intent.putExtra("justificatif_fourni", noteFrais.justificatifFourni)
                intent.putExtra("urgence", noteFrais.urgence)

                // Démarrer l'activité de validation avec attente de résultat
                startActivityForResult(intent, REQUEST_CODE_VALIDATION)
            }
        }

        binding.btnReset.setOnClickListener {
            resetForm()
        }

        // Bouton Historique - Préparé pour le TP4
        binding.btnHistorique.setOnClickListener {
            Toast.makeText(this, "Historique - Disponible dans le TP4", Toast.LENGTH_SHORT).show()
        }
    }

    private fun validateNom(): Boolean {
        return if (noteFrais.isNomValide()) {
            binding.textViewErreurNom.visibility = View.GONE
            true
        } else {
            binding.textViewErreurNom.text = "Le nom doit contenir au moins 2 mots"
            binding.textViewErreurNom.visibility = View.VISIBLE
            false
        }
    }

    private fun validateNumero(): Boolean {
        return if (noteFrais.isNumeroValide()) {
            binding.textViewErreurNumero.visibility = View.GONE
            true
        } else {
            binding.textViewErreurNumero.text = "Le numéro doit contenir exactement 5 chiffres"
            binding.textViewErreurNumero.visibility = View.VISIBLE
            false
        }
    }

    private fun validateForm(): Boolean {
        val isNomValid = noteFrais.isNomValide()
        val isNumeroValid = noteFrais.isNumeroValide()
        val hasDepartement = noteFrais.departement.isNotEmpty()

        val isFormValid = isNomValid && isNumeroValid && hasDepartement
        binding.btnSoumettre.isEnabled = isFormValid

        return isFormValid
    }

    private fun updateNote() {
        // Mise à jour de la prévisualisation avec les données du modèle
        binding.textViewNomNote.text = if (noteFrais.nomEmploye.isNotBlank()) {
            noteFrais.nomEmploye
        } else {
            "Nom employé"
        }

        binding.textViewNumeroNote.text = if (noteFrais.numeroEmploye.isNotBlank()) {
            "N° ${noteFrais.numeroEmploye}"
        } else {
            "N° 00000"
        }

        binding.textViewDepartementNote.text = if (noteFrais.departement.isNotEmpty()) {
            noteFrais.departement
        } else {
            "Département non sélectionné"
        }

        binding.textViewTypeFraisNote.text = noteFrais.typeFrais

        // Mise à jour des montants
        binding.textViewMontantNote.text = "Montant HT: ${String.format("%.2f", noteFrais.montant)}€"
        val montantTTC = noteFrais.calculerMontantTTC()
        binding.textViewMontantTTC.text = "Montant TTC: ${String.format("%.2f", montantTTC)}€"

        // Mise à jour de la ProgressBar (simulation du budget)
        val pourcentageBudget = ((noteFrais.montant / 1000.0) * 100).toInt().coerceAtMost(100)
        binding.progressBarBudget.progress = pourcentageBudget

        validateForm()
    }

    private fun resetForm() {
        // Réinitialiser le modèle
        noteFrais.nomEmploye = ""
        noteFrais.numeroEmploye = ""
        noteFrais.departement = ""
        noteFrais.typeFrais = typesFrais[0]
        noteFrais.montant = 10.0
        noteFrais.avecTVA = false
        noteFrais.fraisRecurrent = false
        noteFrais.justificatifFourni = false
        noteFrais.urgence = "Normal"

        // Réinitialiser l'interface
        binding.editTextNom.text.clear()
        binding.editTextNumero.text.clear()
        binding.radioGroupDepartement.clearCheck()
        binding.spinnerTypeFrais.setSelection(0)
        binding.seekBarMontant.progress = 0
        binding.textViewMontant.text = "Montant : 10€"
        binding.checkBoxRecurrent.isChecked = false
        binding.switchTVA.isChecked = false
        binding.checkBoxJustificatif.isChecked = false
        binding.radioGroupUrgence.check(R.id.radioBtnNormal)

        // Masquer les erreurs
        binding.textViewErreurNom.visibility = View.GONE
        binding.textViewErreurNumero.visibility = View.GONE

        // Reset de la couleur
        binding.notePreview.setBackgroundColor(Color.parseColor(noteFrais.getCouleurUrgence()))

        updateNote()
    }

    // Gestion du retour de ValidationActivity
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_VALIDATION && resultCode == RESULT_OK && data != null) {
            // Récupérer les données de validation
            val statut = data.getStringExtra("statut_validation") ?: "En attente"
            val commentaires = data.getStringExtra("commentaires_manager") ?: ""
            val prioritaire = data.getBooleanExtra("remboursement_prioritaire", false)
            val delai = data.getStringExtra("delai_traitement") ?: "48h"

            // Mettre à jour la note avec les informations de validation
            noteFrais.statut = statut
            noteFrais.commentairesManager = commentaires
            noteFrais.remboursementPrioritaire = prioritaire
            noteFrais.delaiTraitement = delai

            // Sauvegarder la note dans le manager
            NoteFraisManager.ajouterNote(noteFrais.copy())

            // Afficher le résultat
            val message = buildString {
                append("Note de frais $statut")
                if (prioritaire) append(" (prioritaire)")
                if (commentaires.isNotEmpty()) {
                    append("\nCommentaire: $commentaires")
                }
            }
            Toast.makeText(this, message, Toast.LENGTH_LONG).show()

            // Réinitialiser le formulaire si approuvé
            if (statut == "Approuvé") {
                resetForm()
            }
        }
    }
}