package com.example.workshop2

import android.content.Intent
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.workshop2.model.Contact
import com.example.workshop2.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val contactsList = mutableListOf<Contact>()
    private lateinit var adapter: ArrayAdapter<Contact>

    // Launcher pour ajouter un contact
    private val addContactLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            result.data?.getParcelableExtra<Contact>("new_contact")?.let { contact ->
                contactsList.add(contact)
                adapter.notifyDataSetChanged()
                updateEmptyState()
                Toast.makeText(this, "Contact ajouté : ${contact.fullName}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViews()
        loadSampleData()
    }

    private fun setupViews() {
        // Configuration simple de l'ArrayAdapter
        adapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_2,
            android.R.id.text1,
            contactsList
        )

        binding.listViewContacts.adapter = adapter

        // Click sur un élément de la liste
        binding.listViewContacts.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, position, _ ->
                val contact = contactsList[position]
                showContactDetails(contact)
            }

        // Click long pour supprimer
        binding.listViewContacts.onItemLongClickListener =
            AdapterView.OnItemLongClickListener { _, _, position, _ ->
                val contact = contactsList[position]
                showDeleteDialog(contact, position)
                true
            }

        // Bouton d'ajout de contact
        binding.btnAddContact.setOnClickListener {
            openAddContact()
        }

        updateEmptyState()
    }

    private fun loadSampleData() {
        // Données d'exemple pour tester l'application
        contactsList.addAll(listOf(
            Contact("Jean", "Dupont", "jean.dupont@email.com", "0123456789"),
            Contact("Marie", "Martin", "marie.martin@email.com", "0123456788"),
            Contact("Pierre", "Durand", "pierre.durand@email.com", "0123456787")
        ))
        adapter.notifyDataSetChanged()
        updateEmptyState()
    }

    private fun openAddContact() {
        val intent = Intent(this, AddContactActivity::class.java)
        addContactLauncher.launch(intent)
    }

    private fun showContactDetails(contact: Contact) {
        AlertDialog.Builder(this)
            .setTitle(contact.fullName)
            .setMessage("Email: ${contact.email}\nTéléphone: ${contact.phone}")
            .setPositiveButton("OK", null)
            .setNeutralButton("Modifier") { _, _ ->
                // TODO: Implémenter la modification
                Toast.makeText(this, "Modification à implémenter", Toast.LENGTH_SHORT).show()
            }
            .show()
    }

    private fun showDeleteDialog(contact: Contact, position: Int) {
        AlertDialog.Builder(this)
            .setTitle("Supprimer le contact")
            .setMessage("Voulez-vous vraiment supprimer ${contact.fullName} ?")
            .setPositiveButton("Supprimer") { _, _ ->
                contactsList.removeAt(position)
                adapter.notifyDataSetChanged()
                updateEmptyState()
                Toast.makeText(this, "Contact supprimé", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Annuler", null)
            .show()
    }

    private fun updateEmptyState() {
        if (contactsList.isEmpty()) {
            binding.listViewContacts.visibility = android.view.View.GONE
            binding.tvEmptyState.visibility = android.view.View.VISIBLE
        } else {
            binding.listViewContacts.visibility = android.view.View.VISIBLE
            binding.tvEmptyState.visibility = android.view.View.GONE
        }
    }
}