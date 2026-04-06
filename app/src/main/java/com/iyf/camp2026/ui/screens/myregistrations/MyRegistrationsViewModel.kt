package com.iyf.camp2026.ui.screens.myregistrations

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iyf.camp2026.data.repository.InscriptionRepository
import com.iyf.camp2026.domain.model.Inscription
import com.iyf.camp2026.utils.PdfGenerator
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class MyRegistrationsViewModel @Inject constructor(
    private val repository: InscriptionRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val inscriptions: StateFlow<List<Inscription>> = _searchQuery
        .debounce(300)
        .flatMapLatest { query ->
            if (query.isBlank()) {
                repository.allInscriptions
            } else {
                repository.searchInscriptions(query)
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _isGeneratingPdf = MutableStateFlow<Long?>(null)
    val isGeneratingPdf: StateFlow<Long?> = _isGeneratingPdf.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun updateSearch(query: String) {
        _searchQuery.value = query
    }

    fun downloadAndOpenPdf(inscription: Inscription) {
        viewModelScope.launch {
            _isGeneratingPdf.value = inscription.id
            try {
                val pdfFile = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
                    PdfGenerator.generateReceipt(context, inscription)
                }
                pdfFile?.let { file ->
                    val uri = FileProvider.getUriForFile(
                        context,
                        "${context.packageName}.fileprovider",
                        file
                    )
                    val openIntent = Intent(Intent.ACTION_VIEW).apply {
                        setDataAndType(uri, "application/pdf")
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    try {
                        context.startActivity(openIntent)
                    } catch (e: Exception) {
                        _error.value = "Aucun lecteur PDF disponible"
                    }
                }
            } catch (e: Exception) {
                Log.e("MyRegistrationsVM", "Erreur PDF", e)
                _error.value = "Erreur génération PDF: ${e.message}"
            } finally {
                _isGeneratingPdf.value = null
            }
        }
    }

    fun sharePdf(inscription: Inscription) {
        viewModelScope.launch {
            try {
                val pdfFile = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
                    PdfGenerator.generateReceipt(context, inscription)
                }
                pdfFile?.let { file ->
                    val uri = FileProvider.getUriForFile(
                        context,
                        "${context.packageName}.fileprovider",
                        file
                    )
                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                        type = "application/pdf"
                        putExtra(Intent.EXTRA_STREAM, uri)
                        putExtra(Intent.EXTRA_TEXT, "Reçu d'inscription Camp IYF 2026 — ${inscription.referenceNumber}")
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    context.startActivity(Intent.createChooser(shareIntent, "Partager via...").apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    })
                }
            } catch (e: Exception) {
                _error.value = "Erreur partage: ${e.message}"
            }
        }
    }

    fun syncPending() {
        viewModelScope.launch {
            repository.syncPendingInscriptions()
        }
    }

    fun clearError() {
        _error.value = null
    }
}
