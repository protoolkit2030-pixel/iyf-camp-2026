package com.iyf.camp2026.ui.screens.registration

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iyf.camp2026.data.repository.InscriptionRepository
import com.iyf.camp2026.data.repository.RepositoryResult
import com.iyf.camp2026.domain.model.*
import com.iyf.camp2026.utils.ReferenceGenerator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RegistrationFormState(
    // Step 1 - Personal Info
    val nom: String = "",
    val prenom: String = "",
    val dateNaissance: String = "",
    val sexe: String = "",
    val telephone: String = "",
    val email: String = "",
    val quartier: String = "",

    // Step 2 - School Info
    val etablissement: String = "",
    val classe: String = "",
    val filiere: String = "",
    val coursSelectionnes: Set<String> = emptySet(),

    // Step 3 - Payment
    val modePaiement: String = "",
    val numeroTransaction: String = "",
    val confirmed: Boolean = false,

    // UI State
    val currentStep: Int = 1,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val savedInscriptionId: Long? = null,
    val savedReferenceNumber: String? = null
)

data class ValidationErrors(
    val nom: String? = null,
    val prenom: String? = null,
    val dateNaissance: String? = null,
    val sexe: String? = null,
    val telephone: String? = null,
    val quartier: String? = null,
    val etablissement: String? = null,
    val classe: String? = null,
    val coursSelectionnes: String? = null,
    val modePaiement: String? = null,
    val numeroTransaction: String? = null,
    val confirmed: String? = null
)

@HiltViewModel
class RegistrationViewModel @Inject constructor(
    private val repository: InscriptionRepository
) : ViewModel() {

    private val _formState = MutableStateFlow(RegistrationFormState())
    val formState: StateFlow<RegistrationFormState> = _formState.asStateFlow()

    private val _validationErrors = MutableStateFlow(ValidationErrors())
    val validationErrors: StateFlow<ValidationErrors> = _validationErrors.asStateFlow()

    // Step 1 updates
    fun updateNom(value: String) = _formState.update { it.copy(nom = value, errorMessage = null) }
    fun updatePrenom(value: String) = _formState.update { it.copy(prenom = value, errorMessage = null) }
    fun updateDateNaissance(value: String) = _formState.update { it.copy(dateNaissance = value) }
    fun updateSexe(value: String) = _formState.update { it.copy(sexe = value) }
    fun updateTelephone(value: String) = _formState.update { it.copy(telephone = value, errorMessage = null) }
    fun updateEmail(value: String) = _formState.update { it.copy(email = value) }
    fun updateQuartier(value: String) = _formState.update { it.copy(quartier = value, errorMessage = null) }

    // Step 2 updates
    fun updateEtablissement(value: String) = _formState.update { it.copy(etablissement = value, errorMessage = null) }
    fun updateClasse(value: String) {
        _formState.update { it.copy(classe = value, filiere = if (needsFiliere(value)) it.filiere else "") }
    }
    fun updateFiliere(value: String) = _formState.update { it.copy(filiere = value) }
    fun toggleCours(cours: String) {
        _formState.update { state ->
            val updated = if (state.coursSelectionnes.contains(cours)) {
                state.coursSelectionnes - cours
            } else {
                state.coursSelectionnes + cours
            }
            state.copy(coursSelectionnes = updated, errorMessage = null)
        }
    }

    // Step 3 updates
    fun updateModePaiement(value: String) = _formState.update { it.copy(modePaiement = value, errorMessage = null) }
    fun updateNumeroTransaction(value: String) = _formState.update { it.copy(numeroTransaction = value, errorMessage = null) }
    fun updateConfirmed(value: Boolean) = _formState.update { it.copy(confirmed = value) }

    fun needsFiliere(classe: String): Boolean {
        return classe in listOf("2nde", "1ère", "Terminale")
    }

    fun nextStep() {
        val state = _formState.value
        val errors = when (state.currentStep) {
            1 -> validateStep1(state)
            2 -> validateStep2(state)
            else -> ValidationErrors()
        }

        if (hasErrors(errors)) {
            _validationErrors.value = errors
            return
        }

        _validationErrors.value = ValidationErrors()
        if (state.currentStep < 3) {
            _formState.update { it.copy(currentStep = it.currentStep + 1) }
        }
    }

    fun previousStep() {
        _formState.update {
            if (it.currentStep > 1) it.copy(currentStep = it.currentStep - 1) else it
        }
        _validationErrors.value = ValidationErrors()
    }

    fun submitRegistration() {
        val state = _formState.value
        val errors = validateStep3(state)

        if (hasErrors(errors)) {
            _validationErrors.value = errors
            return
        }

        viewModelScope.launch {
            _formState.update { it.copy(isLoading = true, errorMessage = null) }

            val inscription = Inscription(
                referenceNumber = ReferenceGenerator.generate(),
                dateHeure = ReferenceGenerator.getCurrentDateTimeFormatted(),
                nom = state.nom.trim(),
                prenom = state.prenom.trim(),
                dateNaissance = state.dateNaissance,
                sexe = state.sexe,
                telephone = state.telephone.trim(),
                email = state.email.trim(),
                quartier = state.quartier.trim(),
                etablissement = state.etablissement.trim(),
                classe = state.classe,
                filiere = state.filiere,
                coursSelectionnes = state.coursSelectionnes.toList(),
                montant = 5000,
                modePaiement = state.modePaiement,
                numeroTransaction = state.numeroTransaction.trim(),
                statut = "Confirmé"
            )

            when (val result = repository.saveInscription(inscription)) {
                is RepositoryResult.Success -> {
                    _formState.update {
                        it.copy(
                            isLoading = false,
                            savedInscriptionId = result.data.id,
                            savedReferenceNumber = result.data.referenceNumber
                        )
                    }
                }
                is RepositoryResult.Error -> {
                    _formState.update {
                        it.copy(isLoading = false, errorMessage = result.message)
                    }
                }
                else -> {}
            }
        }
    }

    private fun validateStep1(state: RegistrationFormState): ValidationErrors {
        return ValidationErrors(
            nom = if (state.nom.isBlank()) "Le nom est obligatoire" else null,
            prenom = if (state.prenom.isBlank()) "Le prénom est obligatoire" else null,
            dateNaissance = if (state.dateNaissance.isBlank()) "La date de naissance est obligatoire" else null,
            sexe = if (state.sexe.isBlank()) "Veuillez sélectionner votre sexe" else null,
            telephone = when {
                state.telephone.isBlank() -> "Le numéro de téléphone est obligatoire"
                !isValidIvorianPhone(state.telephone) -> "Format invalide. Ex: +225 07 XX XX XX XX"
                else -> null
            },
            quartier = if (state.quartier.isBlank()) "Le quartier/commune est obligatoire" else null
        )
    }

    private fun validateStep2(state: RegistrationFormState): ValidationErrors {
        return ValidationErrors(
            etablissement = if (state.etablissement.isBlank()) "L'établissement est obligatoire" else null,
            classe = if (state.classe.isBlank()) "La classe est obligatoire" else null,
            coursSelectionnes = if (state.coursSelectionnes.isEmpty()) "Sélectionnez au moins un cours" else null
        )
    }

    private fun validateStep3(state: RegistrationFormState): ValidationErrors {
        return ValidationErrors(
            modePaiement = if (state.modePaiement.isBlank()) "Veuillez sélectionner un mode de paiement" else null,
            numeroTransaction = if (state.modePaiement == "Mobile Money" && state.numeroTransaction.isBlank())
                "Le numéro de transaction est obligatoire pour Mobile Money" else null,
            confirmed = if (!state.confirmed) "Veuillez confirmer votre inscription" else null
        )
    }

    private fun hasErrors(errors: ValidationErrors): Boolean {
        return listOf(
            errors.nom, errors.prenom, errors.dateNaissance, errors.sexe,
            errors.telephone, errors.quartier, errors.etablissement,
            errors.classe, errors.coursSelectionnes, errors.modePaiement,
            errors.numeroTransaction, errors.confirmed
        ).any { it != null }
    }

    private fun isValidIvorianPhone(phone: String): Boolean {
        val cleaned = phone.replace(" ", "").replace("-", "")
        return cleaned.matches(Regex("^(\\+225|00225|225)?[0-9]{10}$"))
    }
}
