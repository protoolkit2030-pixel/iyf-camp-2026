package com.iyf.camp2026.ui.screens.home

import androidx.compose.animation.*
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.iyf.camp2026.ui.theme.*

@Composable
fun HomeScreen(
    onNavigateToRegistration: () -> Unit,
    onNavigateToMyRegistrations: () -> Unit
) {
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            HomeTopBar()
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState)
                .background(OffWhite)
        ) {
            // ── Camp Banner ───────────────────────────────────────────
            CampBannerSection()

            Spacer(modifier = Modifier.height(24.dp))

            // ── Action Buttons ─────────────────────────────────────────
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Primary CTA
                Button(
                    onClick = onNavigateToRegistration,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(58.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = OrangeIYF
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.AppRegistration,
                        contentDescription = null,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "S'INSCRIRE MAINTENANT",
                        style = MaterialTheme.typography.labelLarge,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }

                // Secondary button
                OutlinedButton(
                    onClick = onNavigateToMyRegistrations,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = GreenDark
                    ),
                    border = BorderStroke(2.dp, GreenDark)
                ) {
                    Icon(
                        imageVector = Icons.Default.List,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "MES INSCRIPTIONS",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // ── About Section ──────────────────────────────────────────
            AboutSection()

            Spacer(modifier = Modifier.height(20.dp))

            // ── Courses Section ────────────────────────────────────────
            CoursesSection()

            Spacer(modifier = Modifier.height(20.dp))

            // ── Footer ─────────────────────────────────────────────────
            FooterSection()

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeTopBar() {
    TopAppBar(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(BlueIYF)
                ) {
                    Text(
                        text = "IYF",
                        color = White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = PoppinsFontFamily
                    )
                }
                Column {
                    Text(
                        text = "Camp IYF 2026",
                        style = MaterialTheme.typography.titleMedium,
                        color = White,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "International Youth Fellowship",
                        style = MaterialTheme.typography.labelSmall,
                        color = White.copy(alpha = 0.8f)
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = OrangeIYF,
            titleContentColor = White
        )
    )
}

@Composable
private fun CampBannerSection() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colors = listOf(OrangeIYF, OrangeDark)
                )
            )
            .padding(horizontal = 20.dp, vertical = 24.dp)
    ) {
        // Decorative circle
        Box(
            modifier = Modifier
                .size(180.dp)
                .align(Alignment.TopEnd)
                .offset(x = 50.dp, y = (-30).dp)
                .clip(CircleShape)
                .background(White.copy(alpha = 0.07f))
        )

        Column(horizontalAlignment = Alignment.Start) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .width(4.dp)
                        .height(24.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(White)
                )
                Text(
                    text = "CAMP D'ÉTUDE ET DE FORMATION",
                    style = MaterialTheme.typography.labelLarge,
                    color = White,
                    letterSpacing = 1.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Le secret de",
                style = MaterialTheme.typography.headlineMedium,
                color = White,
                fontWeight = FontWeight.Normal
            )
            Text(
                text = "LA RÉUSSITE",
                style = MaterialTheme.typography.displaySmall,
                color = White,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "dans les études",
                style = MaterialTheme.typography.headlineMedium,
                color = White,
                fontWeight = FontWeight.Normal
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Info chips
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                InfoChip(icon = Icons.Default.CalendarMonth, text = "08-10 Avr 2026")
                InfoChip(icon = Icons.Default.LocationOn, text = "CSM Niangon")
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                InfoChip(icon = Icons.Default.AttachMoney, text = "5 000 FCFA")
            }
        }
    }
}

@Composable
private fun InfoChip(icon: ImageVector, text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(White.copy(alpha = 0.2f))
            .padding(horizontal = 10.dp, vertical = 5.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = White,
            modifier = Modifier.size(14.dp)
        )
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = White,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun AboutSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
    ) {
        SectionHeader(title = "À PROPOS DU CAMP")

        Spacer(modifier = Modifier.height(12.dp))

        val activities = listOf(
            Triple(Icons.Default.School, "Conférences & Formations", "Par des experts et professionnels"),
            Triple(Icons.Default.Groups, "Partage d'expérience", "Entre élèves et étudiants"),
            Triple(Icons.Default.Stars, "Prestations & Activités", "Jeux, animations et surprises"),
            Triple(Icons.Default.Restaurant, "Repas assurés", "Restauration incluse sur place")
        )

        activities.forEach { (icon, title, subtitle) ->
            ActivityCard(icon = icon, title = title, subtitle = subtitle)
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun ActivityCard(icon: ImageVector, title: String, subtitle: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(OrangeBackground)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = OrangeIYF,
                    modifier = Modifier.size(22.dp)
                )
            }
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = NearBlack
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MediumGray
                )
            }
        }
    }
}

@Composable
private fun CoursesSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
    ) {
        SectionHeader(title = "COURS DISPENSÉS")

        Spacer(modifier = Modifier.height(12.dp))

        val courses = listOf(
            Triple("FR", "Français", "Littérature & Expression"),
            Triple("φ", "Philosophie", "Réflexion & Argumentation"),
            Triple("∑", "Mathématiques", "Logique & Calcul"),
            Triple("⚗", "Physique-Chimie", "Sciences expérimentales")
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            courses.take(2).forEach { (abbr, name, desc) ->
                CourseCard(abbr = abbr, name = name, desc = desc, modifier = Modifier.weight(1f))
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            courses.drop(2).forEach { (abbr, name, desc) ->
                CourseCard(abbr = abbr, name = name, desc = desc, modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun CourseCard(abbr: String, name: String, desc: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(GreenDark)
            ) {
                Text(
                    text = abbr,
                    color = White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = PoppinsFontFamily
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = name,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                color = NearBlack
            )
            Text(
                text = desc,
                style = MaterialTheme.typography.labelSmall,
                color = MediumGray,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun FooterSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(GreenDark)
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Contactez-nous",
            style = MaterialTheme.typography.titleMedium,
            color = White,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(12.dp))

        val contacts = listOf(
            "+225 07 59 87 21 26",
            "+225 07 49 54 56 06",
            "+225 05 65 65 44 70"
        )

        contacts.forEach { phone ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Phone,
                    contentDescription = null,
                    tint = OrangeLight,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = phone,
                    style = MaterialTheme.typography.bodyMedium,
                    color = White
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Divider(color = White.copy(alpha = 0.2f))

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "© 2026 International Youth Fellowship",
            style = MaterialTheme.typography.labelSmall,
            color = White.copy(alpha = 0.6f),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun SectionHeader(title: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box(
            modifier = Modifier
                .width(4.dp)
                .height(20.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(OrangeIYF)
        )
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = GreenDark,
            letterSpacing = 0.5.sp
        )
    }
}
