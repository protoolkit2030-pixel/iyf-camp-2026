package com.iyf.camp2026.ui.screens.myregistrations

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.iyf.camp2026.domain.model.Inscription
import com.iyf.camp2026.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyRegistrationsScreen(
    onNavigateBack: () -> Unit,
    viewModel: MyRegistrationsViewModel = hiltViewModel()
) {
    val inscriptions by viewModel.inscriptions.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val isGeneratingPdf by viewModel.isGeneratingPdf.collectAsState()
    val error by viewModel.error.collectAsState()

    // Auto-sync on open
    LaunchedEffect(Unit) { viewModel.syncPending() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "Mes inscriptions",
                            style = MaterialTheme.typography.titleMedium,
                            color = White,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            "${inscriptions.size} inscription(s)",
                            style = MaterialTheme.typography.labelSmall,
                            color = White.copy(alpha = 0.8f)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, null, tint = White)
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.syncPending() }) {
                        Icon(Icons.Default.Sync, null, tint = White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = GreenDark)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(OffWhite)
        ) {
            // Search bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = viewModel::updateSearch,
                placeholder = { Text("Rechercher par nom ou référence…", style = MaterialTheme.typography.bodySmall) },
                leadingIcon = { Icon(Icons.Default.Search, null, tint = GreenDark) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.updateSearch("") }) {
                            Icon(Icons.Default.Close, null, tint = MediumGray)
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = GreenDark,
                    unfocusedBorderColor = LightGray,
                    unfocusedContainerColor = White,
                    focusedContainerColor = White
                ),
                singleLine = true
            )

            // Error
            error?.let { err ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(ErrorRed.copy(alpha = 0.1f))
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Error, null, tint = ErrorRed, modifier = Modifier.size(16.dp))
                    Text(err, style = MaterialTheme.typography.bodySmall, color = ErrorRed)
                    Spacer(Modifier.weight(1f))
                    IconButton(onClick = viewModel::clearError, modifier = Modifier.size(20.dp)) {
                        Icon(Icons.Default.Close, null, tint = ErrorRed)
                    }
                }
                Spacer(Modifier.height(8.dp))
            }

            if (inscriptions.isEmpty()) {
                EmptyState(searchQuery = searchQuery)
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(inscriptions, key = { it.id }) { inscription ->
                        InscriptionCard(
                            inscription = inscription,
                            isGeneratingPdf = isGeneratingPdf == inscription.id,
                            onDownloadPdf = { viewModel.downloadAndOpenPdf(inscription) },
                            onSharePdf = { viewModel.sharePdf(inscription) }
                        )
                    }
                    item { Spacer(Modifier.height(16.dp)) }
                }
            }
        }
    }
}

@Composable
private fun EmptyState(searchQuery: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = if (searchQuery.isNotEmpty()) Icons.Default.SearchOff else Icons.Default.Assignment,
            contentDescription = null,
            tint = LightGray,
            modifier = Modifier.size(80.dp)
        )
        Spacer(Modifier.height(16.dp))
        Text(
            text = if (searchQuery.isNotEmpty())
                "Aucun résultat pour \"$searchQuery\""
            else
                "Aucune inscription",
            style = MaterialTheme.typography.titleMedium,
            color = MediumGray,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = if (searchQuery.isNotEmpty())
                "Essayez avec un autre nom ou numéro de référence"
            else
                "Vos inscriptions apparaîtront ici après votre première inscription au Camp IYF 2026.",
            style = MaterialTheme.typography.bodySmall,
            color = MediumGray,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun InscriptionCard(
    inscription: Inscription,
    isGeneratingPdf: Boolean,
    onDownloadPdf: () -> Unit,
    onSharePdf: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "${inscription.prenom} ${inscription.nom}",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = NearBlack,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = inscription.referenceNumber,
                        style = MaterialTheme.typography.labelSmall,
                        color = OrangeIYF,
                        fontWeight = FontWeight.Medium,
                        letterSpacing = 0.5.sp
                    )
                }

                // Sync badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            if (inscription.synced) SuccessGreen.copy(0.12f) else WarningYellow.copy(0.2f)
                        )
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = if (inscription.synced) Icons.Default.CloudDone else Icons.Default.CloudOff,
                            contentDescription = null,
                            tint = if (inscription.synced) SuccessGreen else Color(0xFFB8860B),
                            modifier = Modifier.size(12.dp)
                        )
                        Text(
                            text = if (inscription.synced) "Synchronisé" else "En attente",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (inscription.synced) SuccessGreen else Color(0xFFB8860B),
                            fontSize = 10.sp
                        )
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            // Info row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                InfoChipSmall(Icons.Default.School, inscription.classe)
                InfoChipSmall(Icons.Default.CalendarMonth, inscription.dateHeure.take(10))
                InfoChipSmall(Icons.Default.AttachMoney, "5 000 FCFA")
            }

            // Expandable details
            if (expanded) {
                Spacer(Modifier.height(10.dp))
                Divider(color = LightGray)
                Spacer(Modifier.height(8.dp))

                DetailRow("Établissement", inscription.etablissement)
                DetailRow("Cours", inscription.coursSelectionnesString)
                DetailRow("Mode paiement", inscription.modePaiement)
                if (inscription.numeroTransaction.isNotEmpty()) {
                    DetailRow("N° Transaction", inscription.numeroTransaction)
                }
                DetailRow("Statut", inscription.statut)
            }

            Spacer(Modifier.height(10.dp))

            // Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = { expanded = !expanded },
                    modifier = Modifier.weight(1f).height(38.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = GreenDark),
                    border = BorderStroke(1.dp, GreenDark),
                    contentPadding = PaddingValues(horizontal = 8.dp)
                ) {
                    Icon(
                        imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(if (expanded) "Moins" else "Détails", fontSize = 12.sp)
                }

                Button(
                    onClick = onDownloadPdf,
                    modifier = Modifier.weight(1f).height(38.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = OrangeIYF),
                    enabled = !isGeneratingPdf,
                    contentPadding = PaddingValues(horizontal = 8.dp)
                ) {
                    if (isGeneratingPdf) {
                        CircularProgressIndicator(modifier = Modifier.size(14.dp), color = White, strokeWidth = 2.dp)
                    } else {
                        Icon(Icons.Default.PictureAsPdf, null, modifier = Modifier.size(16.dp))
                    }
                    Spacer(Modifier.width(4.dp))
                    Text("Reçu PDF", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                }

                IconButton(
                    onClick = onSharePdf,
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(GreenDark)
                ) {
                    Icon(Icons.Default.Share, null, tint = White, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}

@Composable
private fun InfoChipSmall(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(3.dp),
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(LightGray)
            .padding(horizontal = 6.dp, vertical = 3.dp)
    ) {
        Icon(icon, null, tint = MediumGray, modifier = Modifier.size(11.dp))
        Text(text, style = MaterialTheme.typography.labelSmall, color = DarkGray, fontSize = 10.sp)
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodySmall, color = MediumGray, modifier = Modifier.width(100.dp))
        Text(
            value.ifBlank { "—" },
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium,
            color = NearBlack,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.End
        )
    }
}
