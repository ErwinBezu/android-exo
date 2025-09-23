package com.example.workshop2
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.workshop2.databinding.ActivityAddContactBinding
import com.example.workshop2.model.Contact

class AddContactActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddContactBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddContactBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViews()
    }

    private fun setupViews() {
        // Configuration du titre
        title = "Nouveau Contact"

        // Bouton Sauvegarder
        binding.btnSave.setOnClickListener {
            if (validateForm()) {
                saveContact()
            }
        }

        // Bouton Annuler
        binding.btnCancel.setOnClickListener {
            finish()
        }

        // Bouton Effacer
        binding.btnClear.setOnClickListener {
            clearForm()
        }
    }

    private fun validateForm(): Boolean {
        var isValid = true

        // Validation du prénom
        val firstName = binding.etFirstName.text.toString().trim()
        if (firstName.isEmpty()) {
            binding.etFirstName.error = "Le prénom est obligatoire"
            isValid = false
        }

        // Validation du nom
        val lastName = binding.etLastName.text.toString().trim()
        if (lastName.isEmpty()) {
            binding.etLastName.error = "Le nom est obligatoire"
            isValid = false
        }

        // Validation de l'email
        val email = binding.etEmail.text.toString().trim()
        if (email.isEmpty()) {
            binding.etEmail.error = "L'email est obligatoire"
            isValid = false
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.etEmail.error = "Format d'email invalide"
            isValid = false
        }

        // Validation du téléphone
        val phone = binding.etPhone.text.toString().trim()
        if (phone.isEmpty()) {
            binding.etPhone.error = "Le téléphone est obligatoire"
            isValid = false
        } else if (phone.length < 10) {
            binding.etPhone.error = "Numéro de téléphone trop court"
            isValid = false
        }

        return isValid
    }

    private fun saveContact() {
        val contact = Contact(
            firstName = binding.etFirstName.text.toString().trim(),
            lastName = binding.etLastName.text.toString().trim(),
            email = binding.etEmail.text.toString().trim(),
            phone = binding.etPhone.text.toString().trim()
        )

        // Retourner le contact à MainActivity
        val resultIntent = Intent().apply {
            putExtra("new_contact", contact)
        }
        setResult(Activity.RESULT_OK, resultIntent)
        finish()
    }

    private fun clearForm() {
        AlertDialog.Builder(this)
            .setTitle("Effacer le formulaire")
            .setMessage("Voulez-vous vraiment effacer tous les champs ?")
            .setPositiveButton("Effacer") { _, _ ->
                binding.etFirstName.setText("")
                binding.etLastName.setText("")
                binding.etEmail.setText("")
                binding.etPhone.setText("")
                binding.etFirstName.requestFocus()
                Toast.makeText(this, "Formulaire effacé", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Annuler", null)
            .show()
    }

    override fun onBackPressed() {
        if (hasUnsavedChanges()) {
            AlertDialog.Builder(this)
                .setTitle("Modifications non sauvegardées")
                .setMessage("Voulez-vous quitter sans sauvegarder ?")
                .setPositiveButton("Quitter") { _, _ ->
                    super.onBackPressed()
                }
                .setNegativeButton("Rester", null)
                .show()
        } else {
            super.onBackPressed()
        }
    }

    private fun hasUnsavedChanges(): Boolean {
        return binding.etFirstName.text.toString().trim().isNotEmpty() ||
                binding.etLastName.text.toString().trim().isNotEmpty() ||
                binding.etEmail.text.toString().trim().isNotEmpty() ||
                binding.etPhone.text.toString().trim().isNotEmpty()
    }
}