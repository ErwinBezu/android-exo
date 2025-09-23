package com.example.notefrais

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.SeekBar
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.notefrais.databinding.ActivityMainBinding
import com.example.notefrais.model.NoteFrais
import com.example.notefrais.model.NoteFraisManager

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val noteFrais = NoteFrais()

    private var isEditingExistingNote = false
    private var editingNoteId = -1

    private val typesFrais = arrayOf("Transport", "Hébergement", "Repas", "Matériel", "Formation")

    private val validationLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.let { data ->
                val action = data.getStringExtra("action")

                if (action == "modifier") {
                    loadModificationData(data)
                } else {
                    val statut = data.getStringExtra("statut_validation")
                    val noteId = data.getIntExtra("note_id", -1)

                    val message = when (statut) {
                        "Approuvé" -> "Note de frais approuvée (ID: $noteId)"
                        "Refusé" -> "Note de frais refusée (ID: $noteId)"
                        else -> "Note de frais en attente de validation (ID: $noteId)"
                    }

                    Toast.makeText(this, message, Toast.LENGTH_LONG).show()

                    if (!isEditingExistingNote) {
                        resetForm()
                    }
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setupViews()
        setupListeners()
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
        // TextWatcher pour le nom
        binding.editTextNom.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                noteFrais.nomEmploye = s.toString()
                validateNom()
                updateNote()
            }
        })

        // TextWatcher pour le numéro
        binding.editTextNumero.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                noteFrais.numeroEmploye = s.toString()
                validateNumero()
                updateNote()
            }
        })

        // RadioGroup département
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

        // Spinner type de frais
        binding.spinnerTypeFrais.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                noteFrais.typeFrais = typesFrais[position]
                updateNote()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        // SeekBar montant
        binding.seekBarMontant.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                noteFrais.montant = 10.0 + progress // 10€ à 500€
                binding.textViewMontant.text = "Montant : ${noteFrais.montant.toInt()}€"
                updateNote()
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        // CheckBox et Switch
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

        // RadioGroup urgence
        binding.radioGroupUrgence.setOnCheckedChangeListener { _, checkedId ->
            noteFrais.urgence = when (checkedId) {
                R.id.radioBtnNormal -> "Normal"
                R.id.radioBtnUrgent -> "Urgent"
                R.id.radioBtnTresUrgent -> "Très urgent"
                else -> "Normal"
            }
            binding.notePreview.setBackgroundColor(Color.parseColor(noteFrais.getCouleurUrgence()))
        }

        // Boutons
        binding.btnCalculer.setOnClickListener {
            val montantTTC = noteFrais.calculerMontantTTC()
            val message = "Montant calculé: ${String.format("%.2f", montantTTC)}€"
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }

        binding.btnSoumettre.setOnClickListener {
            if (validateForm()) {
                openValidationActivity()
            } else {
                Toast.makeText(this, "Veuillez remplir tous les champs obligatoires", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnReset.setOnClickListener {
            resetForm()
        }
    }

    private fun openValidationActivity() {
        val intent = Intent(this, ValidationActivity::class.java)

        if (isEditingExistingNote && editingNoteId != -1) {
            intent.putExtra("note_id", editingNoteId)
        } else {
            intent.putExtra("nom_employe", noteFrais.nomEmploye)
            intent.putExtra("numero_employe", noteFrais.numeroEmploye)
            intent.putExtra("departement", noteFrais.departement)
            intent.putExtra("type_frais", noteFrais.typeFrais)
            intent.putExtra("montant", noteFrais.montant)
            intent.putExtra("avec_tva", noteFrais.avecTVA)
            intent.putExtra("frais_recurrent", noteFrais.fraisRecurrent)
            intent.putExtra("justificatif_fourni", noteFrais.justificatifFourni)
            intent.putExtra("urgence", noteFrais.urgence)
        }

        validationLauncher.launch(intent)
    }

    private fun loadModificationData(data: Intent) {
        isEditingExistingNote = true
        editingNoteId = data.getIntExtra("note_id", -1)

        noteFrais.nomEmploye = data.getStringExtra("nom_employe") ?: ""
        noteFrais.numeroEmploye = data.getStringExtra("numero_employe") ?: ""
        noteFrais.departement = data.getStringExtra("departement") ?: ""
        noteFrais.typeFrais = data.getStringExtra("type_frais") ?: ""
        noteFrais.montant = data.getDoubleExtra("montant", 10.0)
        noteFrais.avecTVA = data.getBooleanExtra("avec_tva", false)
        noteFrais.fraisRecurrent = data.getBooleanExtra("frais_recurrent", false)
        noteFrais.justificatifFourni = data.getBooleanExtra("justificatif_fourni", false)
        noteFrais.urgence = data.getStringExtra("urgence") ?: "Normal"

        fillForm()

        Toast.makeText(this, "Mode modification activé", Toast.LENGTH_SHORT).show()
    }

    private fun fillForm() {
        binding.editTextNom.setText(noteFrais.nomEmploye)
        binding.editTextNumero.setText(noteFrais.numeroEmploye)

        when (noteFrais.departement) {
            "Commercial" -> binding.radioBtnCommercial.isChecked = true
            "Marketing" -> binding.radioBtnMarketing.isChecked = true
            "IT" -> binding.radioBtnIT.isChecked = true
            "RH" -> binding.radioBtnRH.isChecked = true
        }

        val typeIndex = typesFrais.indexOf(noteFrais.typeFrais)
        if (typeIndex != -1) {
            binding.spinnerTypeFrais.setSelection(typeIndex)
        }

        binding.seekBarMontant.progress = (noteFrais.montant - 10.0).toInt()
        binding.textViewMontant.text = "Montant : ${noteFrais.montant.toInt()}€"

        binding.checkBoxRecurrent.isChecked = noteFrais.fraisRecurrent
        binding.switchTVA.isChecked = noteFrais.avecTVA
        binding.checkBoxJustificatif.isChecked = noteFrais.justificatifFourni

        when (noteFrais.urgence) {
            "Normal" -> binding.radioBtnNormal.isChecked = true
            "Urgent" -> binding.radioBtnUrgent.isChecked = true
            "Très urgent" -> binding.radioBtnTresUrgent.isChecked = true
        }

        updateNote()
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
        // Mise à jour nom
        binding.textViewNomNote.text = if (noteFrais.nomEmploye.isNotBlank()) {
            noteFrais.nomEmploye
        } else {
            "Nom employé"
        }

        // Mise à jour numéro
        binding.textViewNumeroNote.text = if (noteFrais.numeroEmploye.isNotBlank()) {
            "N° ${noteFrais.numeroEmploye}"
        } else {
            "N° 00000"
        }

        // Mise à jour département
        binding.textViewDepartementNote.text = if (noteFrais.departement.isNotEmpty()) {
            noteFrais.departement
        } else {
            "Département non sélectionné"
        }

        // Mise à jour type de frais
        binding.textViewTypeFraisNote.text = noteFrais.typeFrais

        // Mise à jour montants
        binding.textViewMontantNote.text = "Montant HT: ${String.format("%.2f", noteFrais.montant)}€"
        val montantTTC = noteFrais.calculerMontantTTC()
        binding.textViewMontantTTC.text = "Montant TTC: ${String.format("%.2f", montantTTC)}€"

        // Mise à jour ProgressBar budget
        binding.progressBarBudget.progress = noteFrais.getPourcentageBudget()

        validateForm()
    }

    private fun resetForm() {
        // Reset du modèle
        noteFrais.nomEmploye = ""
        noteFrais.numeroEmploye = ""
        noteFrais.departement = ""
        noteFrais.typeFrais = typesFrais[0]
        noteFrais.montant = 10.0
        noteFrais.avecTVA = false
        noteFrais.fraisRecurrent = false
        noteFrais.justificatifFourni = false
        noteFrais.urgence = "Normal"

        // Reset des variables d'édition
        isEditingExistingNote = false
        editingNoteId = -1

        // Reset de l'interface
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

        // Masquer erreurs
        binding.textViewErreurNom.visibility = View.GONE
        binding.textViewErreurNumero.visibility = View.GONE

        // Reset couleur
        binding.notePreview.setBackgroundColor(Color.parseColor(noteFrais.getCouleurUrgence()))

        updateNote()
    }
}