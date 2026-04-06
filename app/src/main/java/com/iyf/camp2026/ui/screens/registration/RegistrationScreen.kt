package com.iyf.camp2026.ui.screens.registration

import android.app.DatePickerDialog
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.foundation.text.KeyboardOptions
import com.iyf.camp2026.domain.model.*
import com.iyf.camp2026.ui.theme.*
import java.util.Calendar

@Composable
fun RegistrationScreen(
    onNavigateToConfirmation: (Long) -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: RegistrationViewModel = hiltViewModel()
) {
    val state by viewModel.formState.collectAsState()
    val errors by viewModel.validationErrors.collectAsState()

    // Navigate when saved
    LaunchedEffect(state.savedInscriptionId) {
        state.savedInscriptionId?.let { id ->
            onNavigateToConfirmation(id)
        }
    }

    Scaffold(
        topBar = {
            RegistrationTopBar(
                currentStep = state.currentStep,
                onBack = {
                    if (state.currentStep > 1) viewModel.previousStep()
                    else onNavigateBack()
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(OffWhite)
        ) {
            // Stepper
            StepperIndicator(currentStep = state.currentStep)

            // Error snackbar
            state.errorMessage?.let { error ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = ErrorRed.copy(alpha = 0.1f)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.Error, null, tint = ErrorRed, modifier = Modifier.size(18.dp))
                        Text(error, style = MaterialTheme.typography.bodySmall, color = ErrorRed)
                    }
                }
            }

            // Content
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(0.dp)
            ) {
                when (state.currentStep) {
                    1 -> Step1Content(state, errors, viewModel)
                    2 -> Step2Content(state, errors, viewModel)
                    3 -> Step3Content(state, errors, viewModel)
                }
            }

            // Bottom Button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(White)
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                Button(
                    onClick = {
                        if (state.currentStep < 3) viewModel.nextStep()
                        else viewModel.submitRegistration()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = OrangeIYF),
                    enabled = !state.isLoading
                ) {
                    if (state.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(22.dp),
                            color = White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = if (state.currentStep < 3) "CONTINUER →" else "CONFIRMER L'INSCRIPTION ✓",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RegistrationTopBar(currentStep: Int, onBack: () -> Unit) {
    val stepTitles = mapOf(
        1 to "Informations personnelles",
        2 to "Informations scolaires",
        3 to "Confirmation & Paiement"
    )
    TopAppBar(
        title = {
            Column {
                Text(
                    text = "Inscription — Étape $currentStep/3",
                    style = MaterialTheme.typography.titleMedium,
                    color = White,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = stepTitles[currentStep] ?: "",
                    style = MaterialTheme.typography.labelSmall,
                    color = White.copy(alpha = 0.8f)
                )
            }
        },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, null, tint = White)
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = OrangeIYF)
    )
}

@Composable
private fun StepperIndicator(currentStep: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(White)
            .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (step in 1..3) {
            val isActive = step == currentStep
            val isCompleted = step < currentStep

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(28.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(
                        when {
                            isCompleted -> SuccessGreen
                            isActive -> OrangeIYF
                            else -> LightGray
                        }
                    )
            ) {
                if (isCompleted) {
                    Icon(Icons.Default.Check, null, tint = White, modifier = Modifier.size(14.dp))
                } else {
                    Text(
                        text = step.toString(),
                        style = MaterialTheme.typography.labelMedium,
                        color = if (isActive) White else MediumGray,
                        fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }

            if (step < 3) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(2.dp)
                        .background(if (step < currentStep) SuccessGreen else LightGray)
                )
            }
        }
    }
}

// ─── STEP 1 ───────────────────────────────────────────────────────────────────
@Composable
private fun Step1Content(
    state: RegistrationFormState,
    errors: ValidationErrors,
    viewModel: RegistrationViewModel
) {
    val context = LocalContext.current

    IYFTextField(
        value = state.nom,
        onValueChange = viewModel::updateNom,
        label = "Nom *",
        icon = Icons.Default.Person,
        error = errors.nom
    )

    IYFTextField(
        value = state.prenom,
        onValueChange = viewModel::updatePrenom,
        label = "Prénom *",
        icon = Icons.Default.Person,
        error = errors.prenom
    )

    // Date Picker
    val calendar = Calendar.getInstance()
    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, day ->
            viewModel.updateDateNaissance("${day.toString().padStart(2, '0')}/${(month + 1).toString().padStart(2, '0')}/$year")
        },
        calendar.get(Calendar.YEAR) - 18,
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    ).apply {
        datePicker.maxDate = calendar.timeInMillis
    }

    IYFDateField(
        value = state.dateNaissance,
        label = "Date de naissance *",
        error = errors.dateNaissance,
        onClick = { datePickerDialog.show() }
    )

    // Sexe Radio
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Sexe *",
            style = MaterialTheme.typography.labelMedium,
            color = if (errors.sexe != null) ErrorRed else GreenDark,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
        )
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            listOf("Masculin", "Féminin").forEach { option ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { viewModel.updateSexe(option) }
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    RadioButton(
                        selected = state.sexe == option,
                        onClick = { viewModel.updateSexe(option) },
                        colors = RadioButtonDefaults.colors(selectedColor = OrangeIYF)
                    )
                    Text(option, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
        errors.sexe?.let { ErrorText(it) }
    }

    IYFTextField(
        value = state.telephone,
        onValueChange = viewModel::updateTelephone,
        label = "Téléphone * (ex: +225 07 XX XX XX XX)",
        icon = Icons.Default.Phone,
        error = errors.telephone,
        keyboardType = KeyboardType.Phone,
        placeholder = "+225 07 00 00 00 00"
    )

    IYFTextField(
        value = state.email,
        onValueChange = viewModel::updateEmail,
        label = "Email (optionnel)",
        icon = Icons.Default.Email,
        keyboardType = KeyboardType.Email
    )

    IYFTextField(
        value = state.quartier,
        onValueChange = viewModel::updateQuartier,
        label = "Quartier / Commune de résidence *",
        icon = Icons.Default.Home,
        error = errors.quartier
    )
}

// ─── STEP 2 ───────────────────────────────────────────────────────────────────
@Composable
private fun Step2Content(
    state: RegistrationFormState,
    errors: ValidationErrors,
    viewModel: RegistrationViewModel
) {
    IYFTextField(
        value = state.etablissement,
        onValueChange = viewModel::updateEtablissement,
        label = "Établissement scolaire *",
        icon = Icons.Default.School,
        error = errors.etablissement
    )

    // Classe Dropdown
    IYFDropdown(
        value = state.classe,
        onValueChange = viewModel::updateClasse,
        label = "Classe / Niveau *",
        options = Classe.values().map { it.label },
        error = errors.classe,
        icon = Icons.Default.Class
    )

    // Filière (only for Lycée)
    if (viewModel.needsFiliere(state.classe)) {
        IYFDropdown(
            value = state.filiere,
            onValueChange = viewModel::updateFiliere,
            label = "Filière",
            options = Filiere.values().map { it.label },
            icon = Icons.Default.MenuBook
        )
    }

    // Cours checkboxes
    Spacer(modifier = Modifier.height(8.dp))
    Text(
        text = "Cours souhaités *",
        style = MaterialTheme.typography.labelMedium,
        color = if (errors.coursSelectionnes != null) ErrorRed else GreenDark,
        fontWeight = FontWeight.SemiBold
    )
    Spacer(modifier = Modifier.height(4.dp))

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(4.dp)) {
            COURS_DISPONIBLES.forEach { cours ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.toggleCours(cours) }
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Checkbox(
                        checked = cours in state.coursSelectionnes,
                        onCheckedChange = { viewModel.toggleCours(cours) },
                        colors = CheckboxDefaults.colors(checkedColor = OrangeIYF)
                    )
                    Text(cours, style = MaterialTheme.typography.bodyMedium, color = NearBlack)
                }
            }
        }
    }
    errors.coursSelectionnes?.let { ErrorText(it) }
    Spacer(modifier = Modifier.height(8.dp))
}

// ─── STEP 3 ───────────────────────────────────────────────────────────────────
@Composable
private fun Step3Content(
    state: RegistrationFormState,
    errors: ValidationErrors,
    viewModel: RegistrationViewModel
) {
    // Summary card
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(Icons.Default.Person, null, tint = OrangeIYF, modifier = Modifier.size(20.dp))
                Text(
                    text = "Récapitulatif de l'inscription",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = GreenDark
                )
            }
            Divider(color = LightGray)
            SummaryRow("Nom complet", "${state.prenom} ${state.nom}")
            SummaryRow("Date de naissance", state.dateNaissance)
            SummaryRow("Sexe", state.sexe)
            SummaryRow("Téléphone", state.telephone)
            if (state.email.isNotBlank()) SummaryRow("Email", state.email)
            SummaryRow("Quartier", state.quartier)
            Divider(color = LightGray)
            SummaryRow("Établissement", state.etablissement)
            SummaryRow("Classe", state.classe)
            if (state.filiere.isNotBlank()) SummaryRow("Filière", state.filiere)
            SummaryRow("Cours", state.coursSelectionnes.joinToString(", "))
        }
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Amount display
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = OrangeBackground),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Default.AttachMoney, null, tint = OrangeIYF)
                Text("Montant à régler", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
            }
            Text(
                text = "5 000 FCFA",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = OrangeIYF
            )
        }
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Mode de paiement
    Text(
        text = "Mode de paiement *",
        style = MaterialTheme.typography.labelMedium,
        color = if (errors.modePaiement != null) ErrorRed else GreenDark,
        fontWeight = FontWeight.SemiBold
    )
    Spacer(modifier = Modifier.height(8.dp))

    listOf("Mobile Money", "Espèces sur place").forEach { mode ->
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .clickable { viewModel.updateModePaiement(mode) }
                .background(if (state.modePaiement == mode) OrangeBackground else White)
                .border(
                    1.dp,
                    if (state.modePaiement == mode) OrangeIYF else LightGray,
                    RoundedCornerShape(8.dp)
                )
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            RadioButton(
                selected = state.modePaiement == mode,
                onClick = { viewModel.updateModePaiement(mode) },
                colors = RadioButtonDefaults.colors(selectedColor = OrangeIYF)
            )
            Column {
                Text(mode, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                if (mode == "Mobile Money") {
                    Text(
                        "MTN / Wave / Orange Money",
                        style = MaterialTheme.typography.bodySmall,
                        color = MediumGray
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
    }
    errors.modePaiement?.let { ErrorText(it) }

    // Transaction number (Mobile Money only)
    if (state.modePaiement == "Mobile Money") {
        IYFTextField(
            value = state.numeroTransaction,
            onValueChange = viewModel::updateNumeroTransaction,
            label = "N° de transaction Mobile Money *",
            icon = Icons.Default.ConfirmationNumber,
            error = errors.numeroTransaction,
            keyboardType = KeyboardType.Number
        )
    }

    Spacer(modifier = Modifier.height(8.dp))

    // Confirmation checkbox
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(if (errors.confirmed != null) ErrorRed.copy(alpha = 0.05f) else White)
            .border(1.dp, if (errors.confirmed != null) ErrorRed else LightGray, RoundedCornerShape(8.dp))
            .clickable { viewModel.updateConfirmed(!state.confirmed) }
            .padding(12.dp),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Checkbox(
            checked = state.confirmed,
            onCheckedChange = viewModel::updateConfirmed,
            colors = CheckboxDefaults.colors(checkedColor = OrangeIYF)
        )
        Text(
            text = "Je confirme mon inscription et j'accepte de payer 5 000 FCFA pour participer au Camp d'Étude et de Formation IYF 2026.",
            style = MaterialTheme.typography.bodySmall,
            color = NearBlack
        )
    }
    errors.confirmed?.let { ErrorText(it) }

    Spacer(modifier = Modifier.height(16.dp))
}

// ─── REUSABLE COMPONENTS ─────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IYFTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    error: String? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    placeholder: String = ""
) {
    Column(modifier = Modifier.padding(vertical = 6.dp)) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label, style = MaterialTheme.typography.labelMedium) },
            leadingIcon = { Icon(icon, null, tint = if (error != null) ErrorRed else OrangeIYF) },
            placeholder = if (placeholder.isNotEmpty()) ({ Text(placeholder, style = MaterialTheme.typography.bodySmall, color = MediumGray) }) else null,
            isError = error != null,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = OrangeIYF,
                focusedLabelColor = OrangeIYF,
                cursorColor = OrangeIYF,
                errorBorderColor = ErrorRed,
                unfocusedBorderColor = LightGray,
                unfocusedContainerColor = White,
                focusedContainerColor = White
            ),
            singleLine = true
        )
        error?.let { ErrorText(it) }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IYFDateField(
    value: String,
    label: String,
    error: String? = null,
    onClick: () -> Unit
) {
    Column(modifier = Modifier.padding(vertical = 6.dp)) {
        // Box wrapper : la couche transparente cliquable par-dessus le TextField
        // corrige le bug où OutlinedTextField consomme le clic avant clickable()
        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = value,
                onValueChange = {},
                label = { Text(label, style = MaterialTheme.typography.labelMedium) },
                leadingIcon = { Icon(Icons.Default.CalendarMonth, null, tint = if (error != null) ErrorRed else OrangeIYF) },
                trailingIcon = { Icon(Icons.Default.KeyboardArrowDown, null, tint = MediumGray) },
                readOnly = true,
                enabled = false,
                isError = error != null,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    // Couleurs "disabled" calquées sur le look normal pour garder l'apparence
                    disabledTextColor = MaterialTheme.colorScheme.onSurface,
                    disabledBorderColor = if (error != null) ErrorRed else LightGray,
                    disabledLeadingIconColor = if (error != null) ErrorRed else OrangeIYF,
                    disabledTrailingIconColor = MediumGray,
                    disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledPlaceholderColor = MediumGray,
                    disabledContainerColor = White,
                    focusedBorderColor = OrangeIYF,
                    unfocusedBorderColor = LightGray,
                    errorBorderColor = ErrorRed,
                    unfocusedContainerColor = White,
                    focusedContainerColor = White,
                ),
                placeholder = { Text("JJ/MM/AAAA", style = MaterialTheme.typography.bodySmall, color = MediumGray) }
            )
            // Couche transparente qui intercepte le clic et ouvre le calendrier
            Box(modifier = Modifier
                .matchParentSize()
                .clickable { onClick() }
            )
        }
        error?.let { ErrorText(it) }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IYFDropdown(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    options: List<String>,
    icon: androidx.compose.ui.graphics.vector.ImageVector = Icons.Default.List,
    error: String? = null
) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier.padding(vertical = 6.dp)) {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = value,
                onValueChange = {},
                readOnly = true,
                label = { Text(label, style = MaterialTheme.typography.labelMedium) },
                leadingIcon = { Icon(icon, null, tint = if (error != null) ErrorRed else OrangeIYF) },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                isError = error != null,
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = OrangeIYF,
                    unfocusedBorderColor = LightGray,
                    errorBorderColor = ErrorRed,
                    unfocusedContainerColor = White,
                    focusedContainerColor = White
                )
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option, style = MaterialTheme.typography.bodyMedium) },
                        onClick = {
                            onValueChange(option)
                            expanded = false
                        }
                    )
                }
            }
        }
        error?.let { ErrorText(it) }
    }
}

@Composable
fun SummaryRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MediumGray,
            modifier = Modifier.weight(0.4f)
        )
        Text(
            text = value.ifBlank { "—" },
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium,
            color = NearBlack,
            modifier = Modifier.weight(0.6f)
        )
    }
}

@Composable
fun ErrorText(message: String) {
    Text(
        text = "⚠ $message",
        style = MaterialTheme.typography.labelSmall,
        color = ErrorRed,
        modifier = Modifier.padding(start = 4.dp, top = 2.dp)
    )
}
