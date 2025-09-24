package com.example.notefrais.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.notefrais.components.ActionButtons
import com.example.notefrais.components.InfoRow
import com.example.notefrais.components.InfoSection
import com.example.notefrais.model.NoteFrais
import com.example.notefrais.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    note: NoteFrais,
    onBackClick: () -> Unit,
    onModifierClick: () -> Unit,
    onSupprimerClick: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Détail Note #${note.id}",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Retour")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .width(6.dp)
                        .height(40.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(getUrgenceColor(note.urgence))
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = note.nomEmploye,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Urgence : ${note.urgence}",
                        fontSize = 14.sp,
                        color = getUrgenceColor(note.urgence),
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            InfoSection(
                title = "Informations générales",
                content = {
                    InfoRow("N° employé", note.numeroEmploye)
                    InfoRow("Département", note.departement)
                    InfoRow("Type de frais", note.typeFrais)
                    InfoRow("Date de création", note.dateCreation)
                }
            )

            InfoSection(
                title = "Montants",
                content = {
                    InfoRow("Montant HT", "${String.format("%.2f", note.montant)}€")
                    InfoRow("Montant TTC", "${String.format("%.2f", note.calculerMontantTTC())}€")
                    InfoRow("TVA récupérable", if (note.avecTVA) "Oui" else "Non")
                }
            )

            val optionsText = buildList {
                if (note.fraisRecurrent) add("Frais récurrent")
                if (note.justificatifFourni) add("Justificatif fourni")
                if (note.remboursementPrioritaire) add("Remboursement prioritaire")
            }

            if (optionsText.isNotEmpty()) {
                InfoSection(
                    title = "Options",
                    content = {
                        optionsText.forEach { option ->
                            InfoRow("✓", option)
                        }
                    }
                )
            }

            InfoSection(
                title = "Statut de validation",
                content = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Statut",
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = getStatutColor(note.statut).copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = note.statut,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                fontSize = 14.sp,
                                color = getStatutColor(note.statut),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    if (note.commentairesManager.isNotBlank()) {
                        InfoRow("Commentaires manager", note.commentairesManager)
                    }

                    if (note.dateValidation.isNotBlank()) {
                        InfoRow("Date de validation", note.dateValidation)
                    }

                    InfoRow("Délai de traitement", note.delaiTraitement)
                }
            )

            ActionButtons(
                statut = note.statut,
                onModifierClick = onModifierClick,
                onSupprimerClick = { showDeleteDialog = true }
            )
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Confirmer la suppression") },
            text = { Text("Êtes-vous sûr de vouloir supprimer cette note de frais ?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        onSupprimerClick()
                    }
                ) {
                    Text("Supprimer", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Annuler")
                }
            }
        )
    }
}