package com.example.workshop3.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EcranAjouterNote(
    onRetour: () -> Unit,
    onSauvegarder: (String, String) -> Unit
) {
    var titre by remember { mutableStateOf("") }
    var contenu by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nouvelle note") },
                navigationIcon = {
                    IconButton(onClick = onRetour) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Retour")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            TextField(
                value = titre,
                onValueChange = { titre = it },
                label = { Text("Titre") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                placeholder = { Text("Entrez le titre de votre note...") }
            )

            TextField(
                value = contenu,
                onValueChange = { contenu = it },
                label = { Text("Contenu") },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                placeholder = { Text("Écrivez votre note ici...") }
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { onSauvegarder(titre, contenu) },
                    enabled = titre.isNotBlank() || contenu.isNotBlank(),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Sauvegarder")
                }

                OutlinedButton(
                    onClick = onRetour,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Annuler")
                }
            }

            if (titre.isBlank() && contenu.isBlank()) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Text(
                        "Saisissez au moins un titre ou du contenu pour sauvegarder la note",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
        }
    }
}