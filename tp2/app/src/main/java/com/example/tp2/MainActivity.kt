package com.example.tp2

import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.tp2.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var noteFrais: NoteFrais

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        noteFrais = NoteFrais()

        setupSpinner()
        setupListeners()
        updatePreview()
    }

    private fun setupSpinner() {
        val typesFrais = arrayOf("Transport", "Hébergement", "Repas", "Matériel", "Formation")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, typesFrais)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerTypeFrais.adapter = adapter
    }

    private fun setupListeners() {
        binding.editTextNomEmploye.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                noteFrais.nomEmploye = s.toString()
                validateNom()
                updatePreview()
            }
        })

        binding.editTextNumeroEmploye.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                noteFrais.numeroEmploye = s.toString()
                validateNumero()
                updatePreview()
            }
        })

        binding.radioGroupDepartement.setOnCheckedChangeListener { _, checkedId ->
            noteFrais.departement = when (checkedId) {
                R.id.radioButtonCommercial -> "Commercial"
                R.id.radioButtonMarketing -> "Marketing"
                R.id.radioButtonIT -> "IT"
                R.id.radioButtonRH -> "RH"
                else -> ""
            }
            updatePreview()
        }

        binding.spinnerTypeFrais.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                noteFrais.typeFrais = parent?.getItemAtPosition(position).toString()
                updatePreview()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        binding.checkBoxFraisRecurrent.setOnCheckedChangeListener { _, isChecked ->
            noteFrais.fraisRecurrent = isChecked
            if (isChecked) {
                Toast.makeText(this, "Frais récurrent activé - Notification spéciale", Toast.LENGTH_SHORT).show()
            }
        }

        binding.switchTVA.setOnCheckedChangeListener { _, isChecked ->
            noteFrais.avecTVA = isChecked
            updatePreview()
        }

        binding.seekBarMontant.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                noteFrais.montant = 10.0 + progress
                binding.textViewMontant.text = "Montant : ${noteFrais.montant.toInt()}€"
                updatePreview()
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        binding.radioGroupUrgence.setOnCheckedChangeListener { _, checkedId ->
            noteFrais.urgence = when (checkedId) {
                R.id.radioButtonNormal -> "Normal"
                R.id.radioButtonUrgent -> "Urgent"
                R.id.radioButtonTresUrgent -> "Très urgent"
                else -> "Normal"
            }
            updateTicketColor()
        }

        binding.checkBoxJustificatif.setOnCheckedChangeListener { _, isChecked ->
            noteFrais.justificatifFourni = isChecked
        }

        binding.checkBoxValidationManager.setOnCheckedChangeListener { _, isChecked ->
            noteFrais.validationManager = isChecked
        }

        binding.buttonCalculer.setOnClickListener {
            calculerTotal()
        }

        binding.buttonSoumettre.setOnClickListener {
            soumettreNote()
        }

        binding.buttonReset.setOnClickListener {
            resetForm()
        }
    }

    private fun validateNom() {
        if (noteFrais.isNomValide()) {
            binding.textInputLayoutNom.error = null
        } else {
            binding.textInputLayoutNom.error = "Nom invalide (Prénom + Nom requis)"
        }
    }

    private fun validateNumero() {
        if (noteFrais.isNumeroValide()) {
            binding.textInputLayoutNumero.error = null
        } else {
            binding.textInputLayoutNumero.error = "Numéro invalide (5 chiffres requis)"
        }
    }

    private fun updatePreview() {
        binding.textViewTicketEmploye.text = "Employé : ${if (noteFrais.nomEmploye.isEmpty()) "-" else noteFrais.nomEmploye}"
        binding.textViewTicketDepartement.text = "Département : ${if (noteFrais.departement.isEmpty()) "-" else noteFrais.departement}"
        binding.textViewTicketTypeFrais.text = "Type : ${if (noteFrais.typeFrais.isEmpty()) "-" else noteFrais.typeFrais}"
        binding.textViewTicketMontantHT.text = "Montant HT : ${String.format("%.2f", noteFrais.montant)}€"
        binding.textViewTicketMontantTTC.text = "Montant TTC : ${String.format("%.2f", noteFrais.calculerMontantTTC())}€"

        val pourcentage = noteFrais.calculerPourcentageBudget()
        binding.textViewPourcentageBudget.text = "Pourcentage du budget mensuel utilisé : $pourcentage%"
        binding.progressBarBudget.progress = pourcentage

        updateTicketColor()
    }

    private fun updateTicketColor() {
        val color = when (noteFrais.urgence) {
            "Normal" -> Color.WHITE
            "Urgent" -> Color.parseColor("#FFF3CD")
            "Très urgent" -> Color.parseColor("#F8D7DA")
            else -> Color.WHITE
        }
        binding.layoutTicket.setBackgroundColor(color)
    }

    private fun calculerTotal() {
        val montantTTC = noteFrais.calculerMontantTTC()
        val message = "Montant total calculé : ${String.format("%.2f", montantTTC)}€"
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    private fun soumettreNote() {
        if (!noteFrais.isNomValide()) {
            Toast.makeText(this, "Veuillez saisir un nom valide", Toast.LENGTH_SHORT).show()
            return
        }

        if (!noteFrais.isNumeroValide()) {
            Toast.makeText(this, "Veuillez saisir un numéro d'employé valide (5 chiffres)", Toast.LENGTH_SHORT).show()
            return
        }

        if (noteFrais.departement.isEmpty()) {
            Toast.makeText(this, "Veuillez sélectionner un département", Toast.LENGTH_SHORT).show()
            return
        }

        val message = buildString {
            append("Note de frais soumise avec succès!\n")
            append("Employé : ${noteFrais.nomEmploye}\n")
            append("Montant TTC : ${String.format("%.2f", noteFrais.calculerMontantTTC())}€\n")
            append("Urgence : ${noteFrais.urgence}")
        }

        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    private fun resetForm() {
        noteFrais.reset()

        binding.editTextNomEmploye.setText("")
        binding.editTextNumeroEmploye.setText("")
        binding.radioGroupDepartement.clearCheck()
        binding.spinnerTypeFrais.setSelection(0)
        binding.checkBoxFraisRecurrent.isChecked = false
        binding.switchTVA.isChecked = false
        binding.seekBarMontant.progress = 0
        binding.textViewMontant.text = "Montant : 10€"
        binding.radioGroupUrgence.check(R.id.radioButtonNormal)
        binding.checkBoxJustificatif.isChecked = false
        binding.checkBoxValidationManager.isChecked = false

        updatePreview()
        Toast.makeText(this, "Formulaire réinitialisé", Toast.LENGTH_SHORT).show()
    }
}