package com.iyf.camp2026.ui.screens.confirmation

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.iyf.camp2026.domain.model.Inscription
import com.iyf.camp2026.ui.theme.*

@Composable
fun ConfirmationScreen(
    inscriptionId: Long,
    onNavigateToHome: () -> Unit,
    viewModel: ConfirmationViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(inscriptionId) {
        viewModel.loadInscription(inscriptionId)
    }

    if (uiState.isLoading) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = OrangeIYF)
        }
        return
    }

    val inscription = uiState.inscription ?: run {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Inscription introuvable", color = ErrorRed)
        }
        return
    }

    Scaffold(
        topBar = {
            ConfirmationTopBar(onHome = onNavigateToHome)
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(OffWhite)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // ── Success Header ─────────────────────────────────────────
            SuccessHeader()

            Spacer(modifier = Modifier.height(8.dp))

            // ── Reference Number ───────────────────────────────────────
            ReferenceCard(reference = inscription.referenceNumber, date = inscription.dateHeure)

            Spacer(modifier = Modifier.height(16.dp))

            // ── Summary ────────────────────────────────────────────────
            InscriptionSummaryCard(inscription = inscription)

            Spacer(modifier = Modifier.height(20.dp))

            // ── PDF Buttons ────────────────────────────────────────────
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ActionButton(
                    onClick = {
                        viewModel.generateAndDownloadPdf()
                        viewModel.openPdf()
                    },
                    icon = Icons.Default.PictureAsPdf,
                    text = if (uiState.isLoadingPdf) "Génération du reçu..." else "TÉLÉCHARGER LE REÇU PDF",
                    enabled = !uiState.isLoadingPdf,
                    containerColor = OrangeIYF,
                    loading = uiState.isLoadingPdf
                )

                ActionButton(
                    onClick = {
                        viewModel.generateAndDownloadPdf()
                        viewModel.sharePdf()
                    },
                    icon = Icons.Default.Share,
                    text = "PARTAGER LE REÇU",
                    enabled = !uiState.isLoadingPdf,
                    containerColor = GreenDark,
                    loading = false
                )

                OutlinedButton(
                    onClick = onNavigateToHome,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = GreenDark),
                    border = BorderStroke(1.5.dp, GreenDark)
                ) {
                    Icon(Icons.Default.Home, null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("RETOUR À L'ACCUEIL", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                }
            }

            // Error message
            uiState.error?.let { error ->
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = error,
                    color = ErrorRed,
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Camp info reminder
            CampInfoReminder()

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ConfirmationTopBar(onHome: () -> Unit) {
    TopAppBar(
        title = {
            Text(
                "Inscription confirmée",
                style = MaterialTheme.typography.titleMedium,
                color = White,
                fontWeight = FontWeight.SemiBold
            )
        },
        navigationIcon = {
            IconButton(onClick = onHome) {
                Icon(Icons.Default.Home, null, tint = White)
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = SuccessGreen)
    )
}

@Composable
private fun SuccessHeader() {
    var scaleAnim by remember { mutableStateOf(0f) }
    val scale by animateFloatAsState(
        targetValue = scaleAnim,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "success_scale"
    )

    LaunchedEffect(Unit) { scaleAnim = 1f }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(SuccessGreen)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(80.dp)
                .scale(scale)
                .clip(CircleShape)
                .background(White)
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = SuccessGreen,
                modifier = Modifier.size(56.dp)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Inscription réussie !",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = White
        )

        Text(
            text = "Bienvenue au Camp IYF 2026",
            style = MaterialTheme.typography.bodyMedium,
            color = White.copy(alpha = 0.85f)
        )
    }
}

@Composable
private fun ReferenceCard(reference: String, date: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = GreenDark),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "NUMÉRO DE RÉFÉRENCE",
                style = MaterialTheme.typography.labelSmall,
                color = Color(0xFFB2DFDB),
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = reference,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = White,
                letterSpacing = 2.sp
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Inscrit le $date",
                style = MaterialTheme.typography.bodySmall,
                color = White.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
private fun InscriptionSummaryCard(inscription: Inscription) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(
                text = "Détails de l'inscription",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = GreenDark
            )

            Divider(color = LightGray, thickness = 1.dp)

            SummaryDetailRow(Icons.Default.Person, "Participant", "${inscription.prenom} ${inscription.nom}")
            SummaryDetailRow(Icons.Default.School, "Établissement", inscription.etablissement)
            SummaryDetailRow(Icons.Default.Class, "Classe", inscription.classe)
            SummaryDetailRow(Icons.Default.MenuBook, "Cours", inscription.coursSelectionnesString)
            SummaryDetailRow(Icons.Default.AttachMoney, "Montant", "5 000 FCFA")
            SummaryDetailRow(Icons.Default.Payment, "Mode paiement", inscription.modePaiement)

            Divider(color = LightGray, thickness = 1.dp)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Statut", style = MaterialTheme.typography.bodySmall, color = MediumGray)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(SuccessGreen.copy(alpha = 0.15f))
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "✓ ${inscription.statut}",
                        style = MaterialTheme.typography.labelSmall,
                        color = SuccessGreen,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun SummaryDetailRow(icon: ImageVector, label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Icon(icon, null, tint = OrangeIYF, modifier = Modifier.size(16.dp).padding(top = 2.dp))
        Text(label, style = MaterialTheme.typography.bodySmall, color = MediumGray, modifier = Modifier.width(90.dp))
        Text(value, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium, color = NearBlack, modifier = Modifier.weight(1f))
    }
}

@Composable
private fun ActionButton(
    onClick: () -> Unit,
    icon: ImageVector,
    text: String,
    enabled: Boolean,
    containerColor: Color,
    loading: Boolean
) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().height(52.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(containerColor = containerColor),
        enabled = enabled
    ) {
        if (loading) {
            CircularProgressIndicator(modifier = Modifier.size(20.dp), color = White, strokeWidth = 2.dp)
            Spacer(Modifier.width(8.dp))
        } else {
            Icon(icon, null, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
        }
        Text(text, fontWeight = FontWeight.Bold, fontSize = 13.sp)
    }
}

@Composable
private fun CampInfoReminder() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = OrangeBackground),
        border = BorderStroke(1.dp, OrangeIYF.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(Icons.Default.Info, null, tint = OrangeIYF, modifier = Modifier.size(20.dp))
            Column {
                Text(
                    "Rappel important",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = OrangeDark
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    "Présentez ce reçu à l'entrée du camp.\n" +
                    "📅 08 au 10 Avril 2026\n" +
                    "📍 Cours Secondaire Méthodiste (CSM) Niangon",
                    style = MaterialTheme.typography.bodySmall,
                    color = DarkGray,
                    lineHeight = 18.sp
                )
            }
        }
    }
}
