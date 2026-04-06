package com.iyf.camp2026.ui.screens.confirmation

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iyf.camp2026.data.repository.InscriptionRepository
import com.iyf.camp2026.domain.model.Inscription
import com.iyf.camp2026.utils.NotificationHelper
import com.iyf.camp2026.utils.PdfGenerator
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

data class ConfirmationUiState(
    val inscription: Inscription? = null,
    val pdfFile: File? = null,
    val isLoadingPdf: Boolean = false,
    val isLoading: Boolean = true,
    val error: String? = null
)

@HiltViewModel
class ConfirmationViewModel @Inject constructor(
    private val repository: InscriptionRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(ConfirmationUiState())
    val uiState: StateFlow<ConfirmationUiState> = _uiState.asStateFlow()

    fun loadInscription(id: Long) {
        viewModelScope.launch {
            try {
                val inscription = repository.getInscriptionById(id)
                _uiState.update { it.copy(inscription = inscription, isLoading = false) }

                // Show confirmation notification
                inscription?.let {
                    NotificationHelper.showConfirmationNotification(context, it.prenom, it.referenceNumber)
                    NotificationHelper.scheduleCampReminders(context)
                }

                // Auto-generate PDF in background
                inscription?.let { generatePdf(it) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message, isLoading = false) }
            }
        }
    }

    fun generateAndDownloadPdf() {
        val inscription = _uiState.value.inscription ?: return
        viewModelScope.launch {
            generatePdf(inscription)
        }
    }

    private suspend fun generatePdf(inscription: Inscription) {
        _uiState.update { it.copy(isLoadingPdf = true) }
        try {
            val pdf = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
                PdfGenerator.generateReceipt(context, inscription)
            }
            _uiState.update { it.copy(pdfFile = pdf, isLoadingPdf = false) }
        } catch (e: Exception) {
            Log.e("ConfirmationVM", "Erreur génération PDF", e)
            _uiState.update { it.copy(isLoadingPdf = false, error = "Erreur génération PDF: ${e.message}") }
        }
    }

    fun sharePdf() {
        val pdfFile = _uiState.value.pdfFile ?: return
        try {
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                pdfFile
            )
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "application/pdf"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(
                    Intent.EXTRA_TEXT,
                    "Mon reçu d'inscription au Camp IYF 2026 — Réf: ${_uiState.value.inscription?.referenceNumber}"
                )
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(Intent.createChooser(shareIntent, "Partager le reçu via...").apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            })
        } catch (e: Exception) {
            Log.e("ConfirmationVM", "Erreur partage PDF", e)
            _uiState.update { it.copy(error = "Erreur lors du partage: ${e.message}") }
        }
    }

    fun openPdf() {
        val pdfFile = _uiState.value.pdfFile ?: return
        try {
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                pdfFile
            )
            val openIntent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "application/pdf")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(openIntent)
        } catch (e: Exception) {
            _uiState.update { it.copy(error = "Aucun lecteur PDF disponible") }
        }
    }
}
