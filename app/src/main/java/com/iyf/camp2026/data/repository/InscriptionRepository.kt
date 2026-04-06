package com.iyf.camp2026.data.repository

import android.content.Context
import android.util.Log
import com.iyf.camp2026.data.local.InscriptionDao
import com.iyf.camp2026.data.local.InscriptionEntity
import com.iyf.camp2026.data.remote.GoogleSheetsService
import com.iyf.camp2026.domain.model.Inscription
import com.iyf.camp2026.utils.NetworkUtils
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

sealed class RepositoryResult<out T> {
    data class Success<T>(val data: T) : RepositoryResult<T>()
    data class Error(val message: String) : RepositoryResult<Nothing>()
    object Loading : RepositoryResult<Nothing>()
}

@Singleton
class InscriptionRepository @Inject constructor(
    private val inscriptionDao: InscriptionDao,
    private val googleSheetsService: GoogleSheetsService,
    @ApplicationContext private val context: Context
) {
    companion object {
        private const val TAG = "InscriptionRepository"
    }

    val allInscriptions: Flow<List<Inscription>> = inscriptionDao
        .getAllInscriptions()
        .map { entities -> entities.map { it.toDomain() } }

    fun searchInscriptions(query: String): Flow<List<Inscription>> =
        inscriptionDao.searchInscriptions(query).map { entities ->
            entities.map { it.toDomain() }
        }

    suspend fun saveInscription(inscription: Inscription): RepositoryResult<Inscription> {
        return try {
            val entity = InscriptionEntity.fromDomain(inscription)
            val id = inscriptionDao.insert(entity)
            val savedInscription = inscription.copy(id = id)

            // Try to sync immediately if network available
            if (NetworkUtils.isNetworkAvailable(context)) {
                val synced = googleSheetsService.appendInscription(savedInscription)
                if (synced) {
                    inscriptionDao.markAsSynced(id)
                    Log.d(TAG, "Inscription ${savedInscription.referenceNumber} synchronisée avec Google Sheets")
                }
            }

            RepositoryResult.Success(savedInscription.copy(id = id))
        } catch (e: Exception) {
            Log.e(TAG, "Erreur sauvegarde inscription", e)
            RepositoryResult.Error("Erreur lors de l'enregistrement: ${e.localizedMessage}")
        }
    }

    suspend fun syncPendingInscriptions() {
        if (!NetworkUtils.isNetworkAvailable(context)) return

        val unsynced = inscriptionDao.getUnsynced()
        Log.d(TAG, "${unsynced.size} inscription(s) en attente de synchronisation")

        for (entity in unsynced) {
            try {
                val success = googleSheetsService.appendInscription(entity.toDomain())
                if (success) {
                    inscriptionDao.markAsSynced(entity.id)
                    Log.d(TAG, "Inscription ${entity.referenceNumber} synchronisée")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Erreur sync inscription ${entity.referenceNumber}", e)
            }
        }
    }

    suspend fun getInscriptionById(id: Long): Inscription? {
        return inscriptionDao.getById(id)?.toDomain()
    }

    suspend fun getInscriptionByReference(ref: String): Inscription? {
        return inscriptionDao.getByReference(ref)?.toDomain()
    }
}
