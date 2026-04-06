package com.iyf.camp2026.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface InscriptionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(inscription: InscriptionEntity): Long

    @Update
    suspend fun update(inscription: InscriptionEntity)

    @Query("SELECT * FROM inscriptions ORDER BY id DESC")
    fun getAllInscriptions(): Flow<List<InscriptionEntity>>

    @Query("SELECT * FROM inscriptions WHERE id = :id")
    suspend fun getById(id: Long): InscriptionEntity?

    @Query("SELECT * FROM inscriptions WHERE referenceNumber = :ref")
    suspend fun getByReference(ref: String): InscriptionEntity?

    @Query("SELECT * FROM inscriptions WHERE synced = 0")
    suspend fun getUnsynced(): List<InscriptionEntity>

    @Query("UPDATE inscriptions SET synced = 1 WHERE id = :id")
    suspend fun markAsSynced(id: Long)

    @Query("UPDATE inscriptions SET statut = :statut WHERE id = :id")
    suspend fun updateStatut(id: Long, statut: String)

    @Query("SELECT * FROM inscriptions WHERE nom LIKE '%' || :query || '%' OR prenom LIKE '%' || :query || '%' OR referenceNumber LIKE '%' || :query || '%'")
    fun searchInscriptions(query: String): Flow<List<InscriptionEntity>>

    @Query("SELECT COUNT(*) FROM inscriptions")
    suspend fun getCount(): Int

    @Delete
    suspend fun delete(inscription: InscriptionEntity)
}
