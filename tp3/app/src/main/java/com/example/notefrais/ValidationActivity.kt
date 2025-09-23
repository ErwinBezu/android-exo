package com.example.notefrais

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.example.notefrais.databinding.ActivityValidationBinding
import com.example.notefrais.model.NoteFrais
import com.example.notefrais.model.NoteFraisManager
import java.text.SimpleDateFormat
import java.util.*

class ValidationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityValidationBinding
    private lateinit var noteFrais: NoteFrais
    private var noteId: Int = -1

    private val delaisTraitement = arrayOf("24h", "48h", "1 semaine", "2 semaines")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityValidationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        recupererDonneesIntent()
        setupViews()
        setupListeners()
        afficherRecapitulatif()
    }

    private fun recupererDonneesIntent() {
        noteId = intent.getIntExtra("note_id", -1)

        if (noteId != -1) {
            noteFrais = NoteFraisManager.getNote(noteId) ?: NoteFrais()
        } else {
            noteFrais = NoteFrais().apply {
                nomEmploye = intent.getStringExtra("nom_employe") ?: ""
                numeroEmploye = intent.getStringExtra("numero_employe") ?: ""
                departement = intent.getStringExtra("departement") ?: ""
                typeFrais = intent.getStringExtra("type_frais") ?: ""
                montant = intent.getDoubleExtra("montant", 10.0)
                avecTVA = intent.getBooleanExtra("avec_tva", false)
                fraisRecurrent = intent.getBooleanExtra("frais_recurrent", false)
                justificatifFourni = intent.getBooleanExtra("justificatif_fourni", false)
                urgence = intent.getStringExtra("urgence") ?: "Normal"
            }
        }
    }

    private fun setupViews() {
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, delaisTraitement)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerDelai.adapter = adapter

        val delaiIndex = delaisTraitement.indexOf(noteFrais.delaiTraitement)
        if (delaiIndex != -1) {
            binding.spinnerDelai.setSelection(delaiIndex)
        }

        when (noteFrais.statut) {
            "Approuvé" -> binding.radioBtnApprouve.isChecked = true
            "Refusé" -> binding.radioBtnRefuse.isChecked = true
            else -> binding.radioBtnEnAttente.isChecked = true
        }

        binding.editTextCommentaires.setText(noteFrais.commentairesManager)
        binding.checkBoxPrioritaire.isChecked = noteFrais.remboursementPrioritaire
    }

    private fun setupListeners() {
        binding.spinnerDelai.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                noteFrais.delaiTraitement = delaisTraitement[position]
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        binding.radioGroupStatut.setOnCheckedChangeListener { _, checkedId ->
            noteFrais.statut = when (checkedId) {
                R.id.radioBtnApprouve -> "Approuvé"
                R.id.radioBtnRefuse -> "Refusé"
                R.id.radioBtnEnAttente -> "En attente"
                else -> "En attente"
            }
        }

        binding.checkBoxPrioritaire.setOnCheckedChangeListener { _, isChecked ->
            noteFrais.remboursementPrioritaire = isChecked
        }

        binding.btnValider.setOnClickListener {
            validerNote()
        }

        binding.btnModifier.setOnClickListener {
            modifierNote()
        }

        binding.btnAnnuler.setOnClickListener {
            annulerValidation()
        }
    }

    private fun afficherRecapitulatif() {
        binding.textViewNomRecap.text = noteFrais.nomEmploye.ifEmpty { "Nom employé" }
        binding.textViewNumeroRecap.text = "N° ${noteFrais.numeroEmploye.ifEmpty { "00000" }}"
        binding.textViewDepartementRecap.text = noteFrais.departement.ifEmpty { "Département non sélectionné" }

        binding.textViewTypeFraisRecap.text = noteFrais.typeFrais
        binding.textViewMontantHTRecap.text = "Montant HT: ${String.format("%.2f", noteFrais.montant)}€"
        binding.textViewMontantTTCRecap.text = "Montant TTC: ${String.format("%.2f", noteFrais.calculerMontantTTC())}€"
        binding.textViewUrgenceRecap.text = "Urgence: ${noteFrais.urgence}"

        binding.textViewRecurrentRecap.text = if (noteFrais.fraisRecurrent) "Frais récurrent: Oui" else "Frais récurrent: Non"
        binding.textViewTVARecap.text = if (noteFrais.avecTVA) "TVA récupérable: Oui" else "TVA récupérable: Non"
        binding.textViewJustificatifRecap.text = if (noteFrais.justificatifFourni) "Justificatif fourni: Oui" else "Justificatif fourni: Non"

        binding.recapContainer.setBackgroundColor(Color.parseColor(noteFrais.getCouleurUrgence()))

        if (noteFrais.dateCreation.isNotEmpty()) {
            binding.textViewDateCreation.text = "Créé le: ${noteFrais.dateCreation}"
            binding.textViewDateCreation.visibility = View.VISIBLE
        } else {
            binding.textViewDateCreation.visibility = View.GONE
        }
    }

    private fun validerNote() {
        noteFrais.commentairesManager = binding.editTextCommentaires.text.toString()

        if (noteFrais.statut != "En attente") {
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            noteFrais.dateValidation = sdf.format(Date())
        }

        if (noteId == -1) {
            noteId = NoteFraisManager.ajouterNote(noteFrais)
        } else {
            NoteFraisManager.updateNote(noteFrais)
        }

        val resultIntent = Intent()
        resultIntent.putExtra("note_id", noteId)
        resultIntent.putExtra("statut_validation", noteFrais.statut)
        resultIntent.putExtra("commentaires", noteFrais.commentairesManager)
        resultIntent.putExtra("remboursement_prioritaire", noteFrais.remboursementPrioritaire)
        resultIntent.putExtra("delai_traitement", noteFrais.delaiTraitement)

        setResult(Activity.RESULT_OK, resultIntent)
        finish()
    }

    private fun modifierNote() {
        val resultIntent = Intent()
        resultIntent.putExtra("action", "modifier")
        resultIntent.putExtra("note_id", noteId)
        resultIntent.putExtra("nom_employe", noteFrais.nomEmploye)
        resultIntent.putExtra("numero_employe", noteFrais.numeroEmploye)
        resultIntent.putExtra("departement", noteFrais.departement)
        resultIntent.putExtra("type_frais", noteFrais.typeFrais)
        resultIntent.putExtra("montant", noteFrais.montant)
        resultIntent.putExtra("avec_tva", noteFrais.avecTVA)
        resultIntent.putExtra("frais_recurrent", noteFrais.fraisRecurrent)
        resultIntent.putExtra("justificatif_fourni", noteFrais.justificatifFourni)
        resultIntent.putExtra("urgence", noteFrais.urgence)

        setResult(Activity.RESULT_OK, resultIntent)
        finish()
    }

    private fun annulerValidation() {
        setResult(Activity.RESULT_CANCELED)
        finish()
    }
}