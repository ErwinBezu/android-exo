package com.example.student

import android.content.SharedPreferences
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.student.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences = getSharedPreferences("carte_etudiant_prefs", MODE_PRIVATE)

        setupSpinner()
        setupListeners()
        loadPreferences()
        updatePreview()
    }

    private fun setupSpinner() {
        val niveaux = arrayOf("L1", "L2", "L3", "M1", "M2")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, niveaux)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerNiveau.adapter = adapter
    }

    private fun setupListeners() {
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                validateFields()
                updatePreview()
            }
        }

        binding.etName.addTextChangedListener(textWatcher)
        binding.etStudentId.addTextChangedListener(textWatcher)

        binding.rbInformatique.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.rgFiliere2.clearCheck()
                updatePreview()
            }
        }

        binding.rbMath.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.rgFiliere2.clearCheck()
                updatePreview()
            }
        }

        binding.rbPhysique.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.rgFiliere.clearCheck()
                updatePreview()
            }
        }

        binding.rbChimie.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.rgFiliere.clearCheck()
                updatePreview()
            }
        }

        binding.spinnerNiveau.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                updateProgressBar(position)
                updatePreview()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        binding.cbBoursier.setOnCheckedChangeListener { _, _ -> updatePreview() }
        binding.switchBibliotheque.setOnCheckedChangeListener { _, _ -> updatePreview() }

        binding.rgCouleur.setOnCheckedChangeListener { _, _ ->
            updateCardColor()
            savePreferences()
        }

        binding.seekbarTextSize.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                updateTextSize()
                savePreferences()
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        binding.cbShowExtraInfo.setOnCheckedChangeListener { _, isChecked ->
            binding.llExtraInfo.visibility = if (isChecked) View.VISIBLE else View.GONE
        }

        binding.btnApercu.setOnClickListener { showPreview() }
        binding.btnGenerer.setOnClickListener { generateCard() }
        binding.btnReset.setOnClickListener { resetForm() }
    }

    private fun validateFields(): Boolean {
        var isValid = true

        val name = binding.etName.text.toString().trim()
        if (name.isEmpty() || name.split(" ").filter { it.isNotEmpty() }.size < 2) {
            binding.tvNameError.text = "Le nom doit contenir au moins 2 mots"
            binding.tvNameError.visibility = View.VISIBLE
            isValid = false
        } else {
            binding.tvNameError.visibility = View.GONE
        }

        val studentId = binding.etStudentId.text.toString().trim()
        if (studentId.length != 8 || !studentId.matches(Regex("\\d{8}"))) {
            binding.tvStudentIdError.text = "Le numéro doit contenir exactement 8 chiffres"
            binding.tvStudentIdError.visibility = View.VISIBLE
            isValid = false
        } else {
            binding.tvStudentIdError.visibility = View.GONE
        }

        binding.btnGenerer.isEnabled = isValid
        return isValid
    }

    private fun updatePreview() {
        val name = binding.etName.text.toString().trim()
        binding.tvPreviewName.text = if (name.isNotEmpty()) name else "Nom de l'étudiant"

        val studentId = binding.etStudentId.text.toString().trim()
        binding.tvPreviewStudentId.text = if (studentId.isNotEmpty()) "N° $studentId" else "N° Étudiant"

        val filiere = when {
            binding.rbInformatique.isChecked -> "Informatique"
            binding.rbMath.isChecked -> "Mathématiques"
            binding.rbPhysique.isChecked -> "Physique"
            binding.rbChimie.isChecked -> "Chimie"
            else -> "Filière"
        }
        binding.tvPreviewFiliere.text = filiere

        val niveau = binding.spinnerNiveau.selectedItem?.toString() ?: "Niveau"
        binding.tvPreviewNiveau.text = niveau

        binding.tvPreviewBoursier.text = if (binding.cbBoursier.isChecked) {
            binding.tvPreviewBoursier.setTextColor(
                ContextCompat.getColor(this, android.R.color.holo_orange_light)
            )
            "Boursier"
        } else {
            binding.tvPreviewBoursier.setTextColor(
                ContextCompat.getColor(this, android.R.color.white)
            )
            "Non boursier"
        }

        binding.tvPreviewBibliotheque.text = "Accès biblio: ${if (binding.switchBibliotheque.isChecked) "OUI" else "NON"}"
    }

    private fun updateProgressBar(niveauPosition: Int) {
        val progress = when (niveauPosition) {
            0 -> 20
            1 -> 40
            2 -> 60
            3 -> 80
            4 -> 100
            else -> 20
        }
        binding.progressNiveau.progress = progress
    }

    private fun updateCardColor() {
        val color = when {
            binding.rbBleu.isChecked -> ContextCompat.getColor(this, R.color.card_blue)
            binding.rbVert.isChecked -> ContextCompat.getColor(this, R.color.card_green)
            binding.rbRouge.isChecked -> ContextCompat.getColor(this, R.color.card_red)
            else -> ContextCompat.getColor(this, R.color.card_blue)
        }

        val drawable = ContextCompat.getDrawable(this, R.drawable.card_background) as? GradientDrawable
        drawable?.setColor(color)
        binding.llCardPreview.background = drawable
    }

    private fun updateTextSize() {
        val baseSize = 12f
        val progress = binding.seekbarTextSize.progress
        val textSize = baseSize + (progress * 2f) // 12sp → 32sp

        binding.tvPreviewName.textSize = textSize + 4f
        binding.tvPreviewStudentId.textSize = textSize
        binding.tvPreviewFiliere.textSize = textSize
        binding.tvPreviewNiveau.textSize = textSize
        binding.tvPreviewBoursier.textSize = textSize - 2f
        binding.tvPreviewBibliotheque.textSize = textSize - 2f
    }

    private fun savePreferences() {
        val editor = sharedPreferences.edit()

        when {
            binding.rbBleu.isChecked -> editor.putString("couleur", "bleu")
            binding.rbVert.isChecked -> editor.putString("couleur", "vert")
            binding.rbRouge.isChecked -> editor.putString("couleur", "rouge")
        }

        editor.putInt("taille_texte", binding.seekbarTextSize.progress)
        editor.apply()
    }

    private fun loadPreferences() {
        val couleur = sharedPreferences.getString("couleur", "bleu")
        when (couleur) {
            "bleu" -> binding.rbBleu.isChecked = true
            "vert" -> binding.rbVert.isChecked = true
            "rouge" -> binding.rbRouge.isChecked = true
        }

        val tailleTexte = sharedPreferences.getInt("taille_texte", 5)
        binding.seekbarTextSize.progress = tailleTexte

        updateCardColor()
        updateTextSize()
    }

    private fun showPreview() {
        if (validateFields()) {
            Toast.makeText(this, "Prévisualisation mise à jour", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Veuillez corriger les erreurs", Toast.LENGTH_SHORT).show()
        }
    }

    private fun generateCard() {
        if (validateFields()) {
            Toast.makeText(this, "Carte générée avec succès !", Toast.LENGTH_LONG).show()
        }
    }

    private fun resetForm() {
        binding.etName.text.clear()
        binding.etStudentId.text.clear()
        binding.rbInformatique.isChecked = true
        binding.spinnerNiveau.setSelection(0)
        binding.cbBoursier.isChecked = false
        binding.switchBibliotheque.isChecked = true
        binding.rbBleu.isChecked = true
        binding.seekbarTextSize.progress = 5
        binding.cbShowExtraInfo.isChecked = true

        binding.tvNameError.visibility = View.GONE
        binding.tvStudentIdError.visibility = View.GONE

        updateCardColor()
        updateTextSize()
        updatePreview()
        updateProgressBar(0)

        Toast.makeText(this, "Formulaire réinitialisé", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}